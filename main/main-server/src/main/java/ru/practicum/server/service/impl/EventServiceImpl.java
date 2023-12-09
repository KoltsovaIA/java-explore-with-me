package ru.practicum.server.service.impl;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.OutgoingDto;
import ru.practicum.main.api.event.EventFullDto;
import ru.practicum.main.api.event.EventShortDto;
import ru.practicum.main.api.event.EventSort;
import ru.practicum.main.api.event.EventStatus;
import ru.practicum.main.api.event.EventUpdateAdminRequest;
import ru.practicum.main.api.event.EventUpdateRequest;
import ru.practicum.main.api.event.EventUpdateUserRequest;
import ru.practicum.main.api.event.NewEventDto;
import ru.practicum.main.api.event.RequestStatusUpdateRequest;
import ru.practicum.main.api.event.RequestStatusUpdateResult;
import ru.practicum.main.api.event.StateAction;
import ru.practicum.main.api.request.ParticipationRequestDto;
import ru.practicum.main.api.request.RequestStatus;
import ru.practicum.pageable.OffsetBasedPageRequest;
import ru.practicum.server.entity.Category;
import ru.practicum.server.entity.Event;
import ru.practicum.server.entity.QEvent;
import ru.practicum.server.entity.QRequest;
import ru.practicum.server.entity.Request;
import ru.practicum.server.entity.User;
import ru.practicum.server.mapper.EventMapper;
import ru.practicum.server.mapper.LocationMapper;
import ru.practicum.server.mapper.RequestMapper;
import ru.practicum.server.model.EventGetAllByAdminParameters;
import ru.practicum.server.model.EventGetAllParameters;
import ru.practicum.server.repository.CategoryRepository;
import ru.practicum.server.repository.EventRepository;
import ru.practicum.server.repository.RequestRepository;
import ru.practicum.server.repository.UserRepository;
import ru.practicum.server.service.EventService;
import ru.practicum.main.util.exception.AlreadyExistsException;
import ru.practicum.main.util.exception.NotFoundException;
import ru.practicum.client.StatClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.aspectj.runtime.internal.Conversions.longValue;
import static ru.practicum.constants.Constants.SORT_BY_ID_ASC;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final RequestMapper requestMapper;
    private final StatClient statClient;

    @Override
    @Transactional
    public EventFullDto create(NewEventDto newEventDto, long userId) {
        User user = getUserById(userId);
        Category category = getCategoryById(newEventDto.getCategory());

        Event event = eventMapper.toEvent(newEventDto, category, user);

        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventStatus.PENDING);

        return eventMapper.toEventFullDto(eventRepository.save(event), 0L);
    }

    @Override
    public EventFullDto getByIdByUser(long eventId, long userId) {
        Event event = getEventById(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            return eventMapper.toEventFullDto(event, getCountConfirmedRequestsByEventId(eventId));
        }

        throw new NotFoundException("Event with ID = " + eventId + " does not exists.");
    }

    @Override
    public Collection<EventShortDto> getAllByUser(long userId, int from, int size) {
        getUserById(userId);

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(QEvent.event.initiator.id.eq(userId));

        Pageable pageable = new OffsetBasedPageRequest(from, size, SORT_BY_ID_ASC);

        return eventRepository.findAll(builder, pageable)
                .getContent()
                .stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateByUser(EventUpdateUserRequest dto, long eventId, long userId) {
        getUserById(userId);

        Event event = getEventById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with ID = " + eventId + " does not exists.");
        }

        if (event.getState().equals(EventStatus.PUBLISHED)) {
            throw new AlreadyExistsException("Can not update event with PUBLISHED status.");
        }

        patchEvent(event, dto);

        if (dto.getStateAction() != null
                && dto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
            event.setState(EventStatus.CANCELED);
        }
        if (dto.getStateAction() != null
                && dto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
            event.setState(EventStatus.PENDING);
        }

        return eventMapper.toEventFullDto(eventRepository.save(event), getCountConfirmedRequestsByEventId(eventId));
    }

    @Override
    public Collection<EventShortDto> getAll(EventGetAllParameters parameters) {
        String text = parameters.getText();
        List<Long> categories = parameters.getCategories();
        Boolean paid = parameters.getPaid();
        LocalDateTime start = parameters.getStart();
        LocalDateTime end = parameters.getEnd();
        Boolean onlyAvailable = parameters.getOnlyAvailable();
        EventSort eventSort = parameters.getEventSort();
        int from = parameters.getFrom();
        int size = parameters.getSize();
        HttpServletRequest httpServletRequest = parameters.getHttpServletRequest();

        BooleanBuilder builder = makeBuilder(Collections.emptyList(),
                categories,
                Collections.emptyList(),
                start,
                end,
                text,
                onlyAvailable,
                paid);

        Pageable pageable;

        if (eventSort.equals(EventSort.EVENT_DATE)) {
            pageable = new OffsetBasedPageRequest(from, size, Sort.by(Sort.Direction.ASC, "eventDate"));
        } else {
            pageable = new OffsetBasedPageRequest(from, size, Sort.by(Sort.Direction.DESC, "views"));
        }

        statClient.postStat(httpServletRequest);

        List<Event> events = new ArrayList<>(eventRepository.findAll(builder, pageable)
                .getContent());
        Comparator<Event> eventComparator = Comparator.comparing(event -> {
                    if (event.getPublishedOn() != null) {
                        return event.getPublishedOn();
                    } else {
                        return LocalDateTime.MAX;
                    }
                }
        );

        Collection<EventShortDto> dtos = events
                .stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());

        if (!dtos.isEmpty()) {
            Event minPublishedOnEvent = events.stream().min(eventComparator).get();
            Map<Long, Long> views = getViews(dtos, minPublishedOnEvent.getPublishedOn());

            dtos.forEach(dto -> dto.setViews(views.get(dto.getId())));
        }
        return dtos;
    }

    @Override
    public Collection<EventFullDto> getAllByAdmin(EventGetAllByAdminParameters parameters) {
        List<Long> users = parameters.getUsers();
        List<EventStatus> states = parameters.getStates();
        List<Long> categories = parameters.getCategories();
        LocalDateTime start = parameters.getStart();
        LocalDateTime end = parameters.getEnd();
        int from = parameters.getFrom();
        int size = parameters.getSize();

        BooleanBuilder builder = makeBuilder(users,
                categories,
                states,
                start,
                end,
                null,
                null,
                null);

        Pageable pageable = new OffsetBasedPageRequest(from, size, SORT_BY_ID_ASC);

        List<Event> eventList = eventRepository.findAll(builder, pageable).getContent();

        Map<Long, Long> confirmedRequestcounterMap = new HashMap<>(getConfirmedRequestCounterMap(eventList));

        return eventList
                .stream()
                .map(element -> eventMapper.toEventFullDto(element,
                        confirmedRequestcounterMap.get(element.getId()) == null ? 0 :
                                confirmedRequestcounterMap.get(element.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getByIdByPublic(long eventId, HttpServletRequest httpServletRequest) {
        Event event = getEventById(eventId);

        if (event.getState() != EventStatus.PUBLISHED) {
            throw new NotFoundException("Event with ID = " + eventId + " already PUBLISHED.");
        }

        statClient.postStat(httpServletRequest);

        EventFullDto dto = eventMapper.toEventFullDto(event, getCountConfirmedRequestsByEventId(eventId));
        dto.setViews((long) statClient.getStats(event.getCreatedOn(),
                        LocalDateTime.now(),
                        List.of("/events/" + eventId),
                        true)
                .size());

        return dto;
    }

    @Override
    @Transactional
    public EventFullDto updateByAdmin(EventUpdateAdminRequest dto, long eventId) {
        Event event = getEventById(eventId);

        if (dto.getStateAction() != null
                && dto.getStateAction().equals(StateAction.PUBLISH_EVENT)
                && !event.getState().equals(EventStatus.PENDING)) {
            throw new AlreadyExistsException("Event with ID = " + eventId + " is not PENDING.");
        }

        if (dto.getStateAction() != null
                && dto.getStateAction().equals(StateAction.REJECT_EVENT)
                && event.getState().equals(EventStatus.PUBLISHED)) {
            throw new AlreadyExistsException("Event with ID = " + eventId + " already PUBLISHED.");
        }

        patchEvent(event, dto);

        if (dto.getStateAction() != null
                && dto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
            event.setPublishedOn(LocalDateTime.now());
            event.setState(EventStatus.PUBLISHED);
            event.setModeratorComment(null);
        }

        if (dto.getStateAction() != null
                && dto.getStateAction().equals(StateAction.REJECT_EVENT)) {
            event.setState(EventStatus.CANCELED);
            event.setModeratorComment(dto.getModeratorComment());
        }

        return eventMapper.toEventFullDto(eventRepository.save(event), getCountConfirmedRequestsByEventId(eventId));
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long eventId, Long userId) {
        getEventById(eventId);

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(QRequest.request.event.id.eq(eventId));

        return StreamSupport.stream(requestRepository.findAll(builder, SORT_BY_ID_ASC).spliterator(), false)
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestStatusUpdateResult updateRequestStatus(RequestStatusUpdateRequest dto, Long eventId, Long userId) {
        getUserById(userId);
        Event event = getEventById(eventId);
        RequestStatus status = dto.getStatus();

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with ID = " + eventId + " can be update only by initiator.");
        }
        if (event.getParticipantLimit() == 0) {
            throw new AlreadyExistsException("Event haven't participant limit.");
        }
        if (!event.getRequestModeration()) {
            throw new AlreadyExistsException("Event not moderated.");
        }
        if (status.equals(RequestStatus.CONFIRMED) &&
                event.getParticipantLimit() - getCountConfirmedRequestsByEventId(eventId) <= 0) {
            throw new AlreadyExistsException("The limit of requests to participate in the event has been reached");
        }

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(QRequest.request.id.in(dto.getRequestIds()));
        builder.and(QRequest.request.event.id.eq(eventId));

        List<Request> requests = StreamSupport
                .stream(requestRepository.findAll(builder, SORT_BY_ID_ASC).spliterator(), false)
                .collect(Collectors.toList());

        switch (status) {
            case CONFIRMED:
                requests.forEach(r -> {
                    if (!r.getStatus().equals(RequestStatus.PENDING)) {
                        throw new AlreadyExistsException("Request with ID = " + r.getId() + " not PENDING.");
                    }
                    if (event.getParticipantLimit() - getCountConfirmedRequestsByEventId(eventId) <= 0) {
                        r.setStatus(RequestStatus.REJECTED);
                    } else {
                        r.setStatus(status);
                    }
                });
                break;
            case REJECTED:
                requests.forEach(r -> {
                    if (!r.getStatus().equals(RequestStatus.PENDING)) {
                        throw new AlreadyExistsException("Request with ID = " + r.getId() + " not PENDING.");
                    }
                    r.setStatus(status);
                });
        }


        eventRepository.save(event);

        return requestMapper.toRequestStatusUpdateResult(requests);
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with ID = " + userId + " does not exists.")
        );
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException("Category with ID = " + categoryId + " does not exists")
        );
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with ID = " + eventId + " does not exists")
        );
    }

    private Event patchEvent(Event event, EventUpdateRequest dto) {
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getAnnotation() != null && !dto.getAnnotation().isBlank()) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            event.setCategory(getCategoryById(dto.getCategory()));
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getLocation() != null && dto.getLocation().getLat() != null && dto.getLocation().getLon() != null) {
            event.setLocation(locationMapper.toLocation(dto.getLocation()));
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        return event;
    }

    private BooleanBuilder makeBuilder(List<Long> users,
                                       List<Long> categories,
                                       List<EventStatus> states,
                                       LocalDateTime start,
                                       LocalDateTime end,
                                       String text,
                                       Boolean onlyAvailable,
                                       Boolean paid) {
        BooleanBuilder builder = new BooleanBuilder();
        QEvent qEvent = QEvent.event;

        if (users != null && !users.isEmpty()) {
            builder.and(qEvent.initiator.id.in(users));
        }

        if (categories != null && !categories.isEmpty()) {
            builder.and(qEvent.category.id.in(categories));
        }

        if (states != null && !states.isEmpty()) {
            builder.and(qEvent.state.in(states));
        }

        if (text != null && !text.isBlank()) {
            builder.and(qEvent.annotation.likeIgnoreCase(text))
                    .or(qEvent.description.likeIgnoreCase(text));
        }

        if (onlyAvailable != null && onlyAvailable) {
            builder.and((QEvent.event.participantLimit.subtract(getCountConfirmedRequestsByEventId(
                    longValue(QEvent.event.id)))).loe(1));
        }

        if (paid != null) {
            builder.and(QEvent.event.paid.eq(paid));
        }

        if (start != null) {
            builder.and(qEvent.eventDate.after(start));
        }

        if (end != null) {
            builder.and(qEvent.eventDate.before(end));
        }

        return builder;
    }

    private Map<Long, Long> getViews(Collection<EventShortDto> dtos, LocalDateTime startTime) {
        List<String> uris = dtos.stream()
                .map(dto -> "/events/" + dto.getId())
                .collect(Collectors.toList());

        List<OutgoingDto> stats = statClient.getStats(startTime,
                LocalDateTime.now(),
                uris,
                true);

        Map<Long, Long> views = new HashMap<>();

        stats.forEach(hit -> {
            String uri = hit.getUri();
            String[] split = uri.split("/");
            String id = split[2];
            Long eventId = Long.parseLong(id);
            views.put(eventId, hit.getHits());
        });

        return views;
    }

    public long getCountConfirmedRequestsByEventId(long eventId) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(QRequest.request.event.id.eq(eventId));
        builder.and(QRequest.request.status.eq(RequestStatus.CONFIRMED));
        return requestRepository.count(builder);
    }

    private Map<Long, Long> getConfirmedRequestCounterMap(List<Event> eventList) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(QRequest.request.event.in(eventList));
        builder.and(QRequest.request.status.eq(RequestStatus.CONFIRMED));
        List<Request> requestList = (List<Request>) requestRepository.findAll(builder);
        return requestList
                .stream()
                .collect(Collectors.groupingBy(map -> map.getEvent().getId(), Collectors.counting()));

    }
}

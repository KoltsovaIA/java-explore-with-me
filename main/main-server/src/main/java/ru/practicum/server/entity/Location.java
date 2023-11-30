package ru.practicum.server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Embeddable
public class Location {
    @NotNull
    @Min(-180)
    @Max(180)
    private Double lat;

    @NotNull
    @Min(-180)
    @Max(180)
    private Double lon;
}

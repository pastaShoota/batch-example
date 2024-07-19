package fr.chevallier31.my_batch.domain;

public record PointsRecord (
    String          activityDate,
    String          fidelityNumber,
    String          firstName,
    String          lastName,
    String          fidelityCode,
    Integer         points,
    String          filler
    ) 
{}
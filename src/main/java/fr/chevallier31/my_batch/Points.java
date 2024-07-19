package fr.chevallier31.my_batch;

public record Points(
    java.util.Date  activityDate,
    String          fidelityNumber,
    String          firstName,
    String          lastName,
    String          fidelityCode,
    Integer         points
) {
    
}

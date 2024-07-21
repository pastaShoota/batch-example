package fr.chevallier31.my_batch;

import org.springframework.batch.item.ItemProcessor;

public class PointsItemProcessor implements ItemProcessor<Points,Points> {

    @Override
    public Points process(Points points) throws Exception {
        points.setFidelityNumber(points.getFidelityNumber().trim());
        points.setFidelityCode(points.getFidelityCode().trim());
        points.setFirstName(points.getFirstName().trim());
        points.setFirstName(points.getFirstName().trim());

        return points;
    }
}

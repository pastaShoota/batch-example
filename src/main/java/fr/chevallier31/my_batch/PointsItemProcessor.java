package fr.chevallier31.my_batch;

import java.text.SimpleDateFormat;

import org.springframework.batch.item.ItemProcessor;

import fr.chevallier31.my_batch.domain.PointsRecord;

public class PointsItemProcessor implements ItemProcessor<PointsRecord,Points> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    @Override
    public Points process(PointsRecord points) throws Exception {
        java.util.Date activiyDate = dateFormat.parse(points.activityDate());
        String fidelityNumber = points.fidelityNumber().trim();
        String fidelityCode = points.fidelityCode().trim();
        String firstName = points.firstName().trim();
        String lastName = points.firstName().trim();

        return new Points(activiyDate, fidelityNumber, firstName, lastName, fidelityCode, points.points());
    }
    
}

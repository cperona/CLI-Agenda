package event.Observer;

import event.dto.EventResponseDTO;

public class EventNotificationConsole implements EventObserver {

    @Override
    public void onEventAlert(EventResponseDTO event, int daysUntil) {
        System.out.println();
        if (daysUntil == 0) {
            System.out.println("  ! TODAY: [" + event.id() + "] " + event.title());
        } else {
            System.out.println("  * In " + daysUntil + " day(s): [" + event.id() + "] " + event.title());
        }
    }
}
package event.Observer;

import event.dto.EventResponseDTO;

public interface EventObserver {
    void onEventAlert(EventResponseDTO event, int daysUntil);
}

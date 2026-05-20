package event.Observer;

public interface EventSubject {
    void addObserver(EventObserver observer);
    void removeObserver(EventObserver observer);
    boolean notifyObservers();
}

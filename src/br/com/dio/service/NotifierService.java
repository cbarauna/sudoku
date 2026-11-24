package br.com.dio.service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class NotifierService {

    private final Map<EventEnum, List<EventListener>> listeners = new EnumMap<>(EventEnum.class);

    public NotifierService() {
        for (var event : EventEnum.values()) {
            listeners.put(event, new ArrayList<>());
        }
    }

    public void subscribe(final EventEnum eventType, final EventListener listener){
        listeners.get(eventType).add(listener);
    }

    public void notify(final EventEnum eventType){
        listeners.get(eventType).forEach(l -> l.update(eventType));
    }

}

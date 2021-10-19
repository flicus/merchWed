package org.schors.merch;

import org.schors.merch.data.Child;
import org.schors.merch.data.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class Storage {

    private final Logger logger = LoggerFactory.getLogger(Storage.class);

    private final Map<String, List<Child>> players = new HashMap<>();

    public boolean isKnownPlayer(String name) {
        return players.containsKey(name);
    }

    public void addPlayer(String name) {
        players.put(name, new ArrayList<>());
    }

    public List<Child> getPlayerChildren(String name) {
        return players.get(name);
    }

    public void addPlayerChild(String name, Child child) {
        players.computeIfAbsent(name, s -> new ArrayList<>()).add(child);
    }

    public Stream<Child> getAllOtherChildren(String name) {
        return players.keySet().stream()
                      .filter(s -> !s.equals(name))
                      .flatMap(s -> players.get(s).stream());
    }

    public void removePairs(List<Pair> pairs) {
        List<Child> list = players.get(pairs.get(0).getFirst().getUsername());
        pairs.stream()
             .forEach(pair -> {
                 list.remove(pair.getFirst());
                 List<Child> seconds = players.get(pair.getSecond().getUsername());
                 seconds.remove(pair.getSecond());
             });
    }

}

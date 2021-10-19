package org.schors.merch;

import org.schors.merch.data.Child;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
public class Storage {

    private Logger logger = LoggerFactory.getLogger(Storage.class);

    private Map<String, List<Child>> players = new HashMap<>();

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

    public void load() {
        admins.clear();
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("admins.ini"));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        properties.keySet().forEach(o -> admins.put((String) o, properties.getProperty((String) o)));
    }

    public void save() {
        Properties properties = new Properties();
        admins.keySet().forEach(s -> properties.setProperty(s, admins.get(s)));
        try {
            properties.store(new FileOutputStream("admins.ini"), "admins");
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void update(String name, String chatId) {
        admins.put(name, chatId);
        save();
    }

    public String getChatId(String name) {
        load();
        return admins.get(name);
    }
}

package org.schors.merch;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

public class Main {

    public void load() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("application.yaml");
        Map<String, Object> obj = yaml.load(inputStream);

    }

    public static void main(String[] args) {
        Collection items = new ArrayList();
        items.add("1");
        items.add("2");
        items.add("3");
        items.add("4");
        items.add("5");
        items.add("6");
        items.add("7");
        items.add("8");
        items.add("9");
        items.add("10");

        Stream<Stream<String>> stream = Util.getTuples(items, 4);

        stream.forEach(o -> o.reduce((o1, o2) -> o1.concat(o2)).ifPresent(System.out::println));

        Main m = new Main();
        m.load();
    }
}

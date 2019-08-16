package com.jesjobom;

import java.util.List;

public class Main {



    public static void main(String[] args) {

        System.out.println("\t===============> INITIALIZING IGNITE 1");
        IgnitePersistence ignitePersistence = new IgnitePersistence();

        await(1000);

        System.out.println("\t===============> ADDING OBJECT 1");
        ignitePersistence.add("key123", "value" + System.currentTimeMillis());

        await(1000);

        System.out.print("\t===============> LISTING OBJECTS 1: ");
        List<Model> values = ignitePersistence.list();

        System.out.println(values.size());

        await(1000);

        System.out.println("\t===============> CLOSING IGNITE 1");
        ignitePersistence.close();

        await(1000);

        System.out.println("\t===============> INITIALIZING IGNITE 2");
        ignitePersistence = new IgnitePersistence();

        System.out.print("\t===============> LISTING OBJECTS 2: ");
        values = ignitePersistence.list();

        System.out.println(values.size());

        await(1000);

        System.out.println("\t===============> FECHANDO IGNITE 2");
        ignitePersistence.close();

        await(1000);
    }

    private static void await(long milis) {
        try {
            Thread.sleep(milis);
        } catch (Exception e) {}
    }
}

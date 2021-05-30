package com.github.mouse0w0.mmr;

import com.github.mouse0w0.mmr.graphics.Renderer2D;

import java.lang.management.ManagementFactory;

public class Main {
    public static void main(String[] args) {
        System.out.println(ManagementFactory.getRuntimeMXBean().getName());
        new Renderer2D().run();
    }
}

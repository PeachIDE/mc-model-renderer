package com.github.mouse0w0.mmr;

import com.github.mouse0w0.mmr.graphics.Renderer;

import java.lang.management.ManagementFactory;

public class ModelRenderer {
    public static void main(String[] args) {
        System.out.println(ManagementFactory.getRuntimeMXBean().getName());
        new Renderer().run();
    }
}

package com.ll.demo_01;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ComponentA {
    private final ComponentB componentB;
    private final ComponentC componentC;
    private final ComponentC componentD;
    private final ComponentC componentE;

    public String action(){
        return "ComponentA action / " + componentB.getAction();
    }
}

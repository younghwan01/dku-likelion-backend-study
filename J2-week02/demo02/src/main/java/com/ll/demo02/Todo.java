package com.ll.demo02;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Todo {
    private Long id;
    private String body;

}

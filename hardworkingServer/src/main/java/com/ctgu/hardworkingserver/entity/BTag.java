package com.ctgu.hardworkingserver.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class BTag  implements Serializable {
    private String tagId;

    private String tagName;

}
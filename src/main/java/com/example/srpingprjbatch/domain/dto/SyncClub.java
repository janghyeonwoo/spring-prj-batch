package com.example.srpingprjbatch.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SyncClub {
    private String golfSeq;
    private String golfName;
    private String golfTelnum;
    private String golfTelnum2;
    private String zipCode;
    private String golfAddr;
    private String golfAddr1;
    private String golfAddr2;
    private String islYn;
    private String picOffice1;
    private String picOfficeCode1;
    private String picOffice2;
    private String picOfficeCode2;
    private String picOffice3;
    private String picOfficeCode3;
    private String useYn;


    public Integer getTislYn(){
        if(this.islYn == null) return null;
        Integer value = 0;
        switch (this.islYn){
            case "도서" : value = 1; break;
            default: value = 2; break;
        };
        return value;
    }


}

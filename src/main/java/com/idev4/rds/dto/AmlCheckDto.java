package com.idev4.rds.dto;

public class AmlCheckDto {

    public CheckDto clnt;

    public CheckDto nom;

    public CheckDto cob;

    public CheckDto clntRel;

    public CheckDto kin;

    @Override
    public String toString() {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}

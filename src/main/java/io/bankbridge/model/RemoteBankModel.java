package io.bankbridge.model;

import lombok.Getter;

@Getter
public class RemoteBankModel {
    private String bic;
    private String countryCode;
    private String auth;
}

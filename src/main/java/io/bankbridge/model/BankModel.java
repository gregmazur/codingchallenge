package io.bankbridge.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BankModel {
	
	private String bic;
	private String name;
	private String countryCode;
	private String auth;

}

package com.bytebreeze.quickdrop.dto.paymentapiresponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SSLCommerzValidatorResponse {
    public String status;
    public String tran_date;
    public String tran_id;
    public String val_id;
    public String amount;
    public String store_amount;
    public String currency;
    public String bank_tran_id;
    public String card_type;
    public String card_no;
    public String card_issuer;
    public String card_brand;
    public String card_issuer_country;
    public String card_issuer_country_code;
    public String currency_type;
    public String currency_amount;
    public String currency_rate;
    public String base_fair;
    public String value_a;
    public String value_b;
    public String value_c;
    public String value_d;
    public String emi_instalment;
    public String emi_amount;
    public String emi_description;
}

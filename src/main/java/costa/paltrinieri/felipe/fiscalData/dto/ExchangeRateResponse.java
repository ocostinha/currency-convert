package costa.paltrinieri.felipe.fiscalData.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ExchangeRateResponse {

    @NotNull
    @NotEmpty
    @Valid
    private List<ExchangeRateData> data;

    public ExchangeRateResponse() {
    }

    public ExchangeRateResponse(List<ExchangeRateData> data) {
        this.data = data;
    }

    public List<ExchangeRateData> getData() {
        return data;
    }

    public void setData(List<ExchangeRateData> data) {
        this.data = data;
    }

    public static class ExchangeRateData {

        @NotNull
        @JsonProperty("record_date")
        private String recordDate;

        @NotNull
        @JsonProperty("country_currency_desc")
        private String countryCurrencyDesc;

        @NotNull
        @JsonProperty("exchange_rate")
        private String exchangeRate;

        @NotNull
        @JsonProperty("effective_date")
        private String effectiveDate;

        public ExchangeRateData() {
        }

        public ExchangeRateData(final String recordDate, final String countryCurrencyDesc, final String exchangeRate, final String effectiveDate) {
            this.recordDate = recordDate;
            this.countryCurrencyDesc = countryCurrencyDesc;
            this.exchangeRate = exchangeRate;
            this.effectiveDate = effectiveDate;
        }

        public String getRecordDate() {
            return recordDate;
        }

        public void setRecordDate(String recordDate) {
            this.recordDate = recordDate;
        }

        public String getCountryCurrencyDesc() {
            return countryCurrencyDesc;
        }

        public void setCountryCurrencyDesc(String countryCurrencyDesc) {
            this.countryCurrencyDesc = countryCurrencyDesc;
        }

        public String getExchangeRate() {
            return exchangeRate;
        }

        public void setExchangeRate(String exchangeRate) {
            this.exchangeRate = exchangeRate;
        }

        public String getEffectiveDate() {
            return effectiveDate;
        }

        public void setEffectiveDate(String effectiveDate) {
            this.effectiveDate = effectiveDate;
        }

    }

}

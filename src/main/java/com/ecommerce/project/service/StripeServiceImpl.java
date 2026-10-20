package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.payload.StripePaymentDTO;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.secret.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public PaymentIntent paymentIntent(StripePaymentDTO stripePaymentDTO) throws StripeException {
        if (stripePaymentDTO.getEmail() == null || stripePaymentDTO.getEmail().isBlank()) {
            throw new APIException("Email is required to create a payment.");
        }

        Customer customer = findOrCreateCustomer(stripePaymentDTO);

        PaymentIntentCreateParams.Builder params = PaymentIntentCreateParams.builder()
                .setAmount(stripePaymentDTO.getAmount())
                .setCurrency(stripePaymentDTO.getCurrency())
                .setDescription(stripePaymentDTO.getDescription())
                .setCustomer(customer.getId())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build());
        if (stripePaymentDTO.getMetadata() != null) {
            params.putAllMetadata(stripePaymentDTO.getMetadata());
        }

        return PaymentIntent.create(params.build());
    }

    private Customer findOrCreateCustomer(StripePaymentDTO stripePaymentDTO) throws StripeException {
        // list() filters by exact email, so the email is never interpolated into a query string
        CustomerListParams listParams = CustomerListParams.builder()
                .setEmail(stripePaymentDTO.getEmail())
                .setLimit(1L)
                .build();
        var existing = Customer.list(listParams).getData();
        if (!existing.isEmpty()) {
            return existing.get(0);
        }

        CustomerCreateParams.Builder createParams = CustomerCreateParams.builder()
                .setName(stripePaymentDTO.getName())
                .setEmail(stripePaymentDTO.getEmail());

        Address address = stripePaymentDTO.getAddress();
        if (address != null) {
            CustomerCreateParams.Address.Builder stripeAddress = CustomerCreateParams.Address.builder()
                    .setLine1(address.getStreet())
                    .setLine2(address.getBuildingName())
                    .setCity(address.getCity())
                    .setState(address.getState())
                    .setPostalCode(address.getPincode());
            String countryCode = toCountryCode(address.getCountry());
            if (countryCode != null) {
                stripeAddress.setCountry(countryCode);
            }
            createParams.setAddress(stripeAddress.build());
        }

        return Customer.create(createParams.build());
    }

    // Stripe only accepts 2-letter ISO country codes, but addresses here store the country name (e.g. "Indonesia").
    private String toCountryCode(String country) {
        if (country == null || country.isBlank()) {
            return null;
        }
        String value = country.trim();
        for (String code : Locale.getISOCountries()) {
            if (code.equalsIgnoreCase(value)
                    || new Locale.Builder().setRegion(code).build().getDisplayCountry(Locale.ENGLISH).equalsIgnoreCase(value)) {
                return code;
            }
        }
        return null;
    }
}

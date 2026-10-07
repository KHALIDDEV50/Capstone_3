package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.Model.User;
import com.example.rafeeq.Model.VitalSign;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TwilioService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.whatsapp-from}")
    private String whatsappFrom;

    @Value("${twilio.content-sid:}")
    private String contentSid;

    @Value("${appointment.booking-url}")
    private String appointmentBookingUrl;

    public String sendCriticalAppointmentWhatsApp(
            User user,
            VitalSign criticalVital) {

        if (user.getWhatsappOptIn() == null
                || !user.getWhatsappOptIn()) {

            throw new ApiException(
                    "WhatsApp notifications are not enabled for this user"
            );
        }

        if (criticalVital == null
                || !"CRITICAL".equals(criticalVital.getFlag())) {

            throw new ApiException(
                    "WhatsApp notification can only be sent for a critical health measurement"
            );
        }

        if (isBlank(accountSid)
                || isBlank(authToken)
                || isBlank(whatsappFrom)) {

            throw new ApiException(
                    "Twilio WhatsApp configuration is incomplete"
            );
        }

        String recipient =
                normalizeSaudiPhone(
                        user.getPhone()
                );

        String bookingUrl =
                appointmentBookingUrl
                        + "?userId="
                        + user.getId();

        try {

            Twilio.init(
                    accountSid.trim(),
                    authToken.trim()
            );

            PhoneNumber to =
                    new PhoneNumber(
                            "whatsapp:" + recipient
                    );

            PhoneNumber from =
                    new PhoneNumber(
                            normalizeWhatsAppFrom(
                                    whatsappFrom
                            )
                    );

            Message message;

            if (!isBlank(contentSid)) {

                // Twilio trial template (fixed text, no variables)
                message =
                        Message.creator(
                                        to,
                                        from,
                                        (String) null
                                )
                                .setContentSid(
                                        cleanSid(contentSid)
                                )
                                .create();

            } else {

                // Free-text message (for a full Twilio account / approved sender)
                message =
                        Message.creator(
                                        to,
                                        from,
                                        buildMessageBody(
                                                user.getFullName(),
                                                getConditionName(
                                                        criticalVital.getType()
                                                ),
                                                getReading(
                                                        criticalVital
                                                ),
                                                bookingUrl
                                        )
                                )
                                .create();
            }

            return message.getSid();

        } catch (Exception e) {

            e.printStackTrace();

            throw new ApiException(
                    "Twilio error: "
                            + e.getMessage()
            );
        }
    }

    private String buildMessageBody(
            String fullName,
            String conditionName,
            String reading,
            String bookingUrl) {

        return "BasirhAI - URGENT health alert\n\n"
                + "Hello " + fullName + ",\n"
                + "A critical " + conditionName
                + " reading was recorded: " + reading + ".\n\n"
                + "Please contact a healthcare professional as soon as possible.\n"
                + "Book an appointment: " + bookingUrl + "\n\n"
                + "If you feel unwell or this is an emergency, call 997.";
    }

    private String cleanSid(
            String sid) {

        // Removes spaces and accidental quotes, e.g. "HX..." -> HX...
        return sid.trim()
                .replace("\"", "")
                .replace("'", "");
    }

    private String normalizeWhatsAppFrom(
            String from) {

        if (from == null
                || from.isBlank()) {

            throw new ApiException(
                    "Twilio WhatsApp sender is required"
            );
        }

        String trimmed =
                from.trim();

        if (trimmed.startsWith("whatsapp:")) {
            return trimmed;
        }

        return "whatsapp:" + trimmed;
    }

    private String normalizeSaudiPhone(
            String phone) {

        if (phone == null
                || phone.isBlank()) {

            throw new ApiException(
                    "User phone number is required for WhatsApp notification"
            );
        }

        String normalized =
                phone.trim()
                        .replace(" ", "")
                        .replace("-", "");

        if (normalized.startsWith("05")
                && normalized.length() == 10) {

            return "+966"
                    + normalized.substring(1);
        }

        if (normalized.startsWith("966")) {

            return "+"
                    + normalized;
        }

        if (normalized.startsWith("+")) {

            return normalized;
        }

        throw new ApiException(
                "User phone number must be in a valid international format"
        );
    }

    private String getConditionName(
            String type) {

        return switch (type) {
            case "BLOOD_PRESSURE" -> "Blood Pressure";
            case "GLUCOSE" -> "Glucose";
            case "HEART_RATE" -> "Heart Rate";
            case "WEIGHT" -> "Weight";
            case "WAIST" -> "Waist";
            default -> type;
        };
    }

    private String getReading(
            VitalSign vitalSign) {

        if ("BLOOD_PRESSURE".equals(
                vitalSign.getType()
        )) {

            return vitalSign.getSystolic()
                    + "/"
                    + vitalSign.getDiastolic()
                    + " "
                    + vitalSign.getUnit();
        }

        return vitalSign.getValue()
                + " "
                + vitalSign.getUnit();
    }

    private boolean isBlank(
            String value) {

        return value == null
                || value.isBlank();
    }
}
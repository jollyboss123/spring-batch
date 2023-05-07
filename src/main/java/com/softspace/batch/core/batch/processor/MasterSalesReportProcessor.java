package com.softspace.batch.core.batch.processor;

import com.softspace.common.constant.TransactionStatus;
import com.softspace.common.util.FormatterUtil;
import com.softspace.fasspos.common.batch.dto.MasterSalesReportDTO;
import com.softspace.fasspos.entity.constant.DiscountTypeEnum;
import com.softspace.fasspos.entity.constant.PromoTypeEnum;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

/**
 * @author Jolly
 */
@Component
public class MasterSalesReportProcessor extends AbstractItemProcessor implements ItemProcessor<MasterSalesReportDTO, MasterSalesReportDTO> {
    @Override
    public MasterSalesReportDTO process(MasterSalesReportDTO masterSalesReportDTO) throws Exception {
        String closeSyncDate = "", closeSyncTime = "", transactionDate = "", transactionTime = "";
        if (masterSalesReportDTO.getCloseSyncDateTime() != null) {
            closeSyncDate = FormatterUtil.getFormatted(masterSalesReportDTO.getCloseSyncDateTime().toInstant(), REPORT_DATE_FORMAT);
            closeSyncTime = FormatterUtil.getFormatted(masterSalesReportDTO.getCloseSyncDateTime().toInstant(), REPORT_TIME_FORMAT);
        }

        if (masterSalesReportDTO.getTransactionDateTime() != null) {
            Instant trxDate = Instant.EPOCH.plusMillis(Long.parseLong(masterSalesReportDTO.getTransactionDateTime()));
            transactionDate = FormatterUtil.getFormatted(trxDate, REPORT_DATE_FORMAT);
            transactionTime = FormatterUtil.getFormatted(trxDate, REPORT_TIME_FORMAT);
        }

        String promoType = "-";
        if (masterSalesReportDTO.getPromoType() != null) {
            promoType = Optional.ofNullable(PromoTypeEnum.getTypeEnum(masterSalesReportDTO.getPromoType()))
                    .map(PromoTypeEnum::getDescription)
                    .orElse("-");
        }

        String promoName = masterSalesReportDTO.getPromoName() != null ? masterSalesReportDTO.getPromoName() : "-";

        String discountType = "-";
        if (masterSalesReportDTO.getDiscountTypeId() != null &&
        masterSalesReportDTO.getDiscountTypeId() != 0) {
            discountType = Optional.ofNullable(DiscountTypeEnum.getTypeEnum(masterSalesReportDTO.getDiscountTypeId()))
                    .map(DiscountTypeEnum::getDescription)
                    .orElse("-");
        }

        String isPromoEntry = masterSalesReportDTO.getIsPromoEntryInBool() != null
                ? (masterSalesReportDTO.getIsPromoEntryInBool() ? "1" : "0")
                : "";

        String authorizationDate = "", authorizationTime = "";
        if (StringUtil.isNotBlank(masterSalesReportDTO.getAuthorizationDateTime())) {
            Instant authDateTime = FormatterUtil.getInstantFromString(masterSalesReportDTO.getAuthorizationDateTime(),
                    FormatterUtil.YYYYMMDDHHMMSS);
            authorizationDate = FormatterUtil.getStringFromInstant(authDateTime, REPORT_DATE_FORMAT);
            authorizationTime = FormatterUtil.getStringFromInstant(authDateTime, REPORT_TIME_FORMAT);
        }

        TransactionStatus transactionStatus = TransactionStatus.valueOf(masterSalesReportDTO.getTransactionStatus());
        String paymentStatus = transactionStatus.getMessage();
        String cardAmount = "", resubmission = "", rejectCode = "";

        // Cash Trx
        if (masterSalesReportDTO.getCreditCardType() == null &&
            (masterSalesReportDTO.getCashPaymentType() != null ||
                    masterSalesReportDTO.getBillGrandTotalInBigDecimal().compareTo(BigDecimal.ZERO) == 0)
        ) {
            paymentStatus = TransactionStatus.VOIDED.equals(transactionStatus)
                    ? TransactionStatus.REFUNDED.getMessage()
                    : paymentStatus;
        }

        // Card trx
        if (masterSalesReportDTO.getCreditCardType() != null &&
            masterSalesReportDTO.getCashPaymentType() == null) {
            paymentStatus = TransactionStatus.VOIDED.equals(transactionStatus)
                    ? TransactionStatus.REFUNDED.getMessage()
                    : Optional.of(TransactionStatus.valueOf(masterSalesReportDTO.getPaymentStatus()))
                        .map(TransactionStatus::getMessage)
                        .orElse("");
            cardAmount = masterSalesReportDTO.getCard();
            resubmission = masterSalesReportDTO.getResubmission();
            rejectCode = masterSalesReportDTO.getRejectCode();
        }

        return MasterSalesReportDTO.builder()
                .billSubtotal(nullCheckForMonetary(masterSalesReportDTO.getBillSubtotalInBigDecimal()))
                .billGrandTotal(nullCheckForMonetary(masterSalesReportDTO.getBillGrandTotalInBigDecimal()))
                .refundReason(nullCheckForString(masterSalesReportDTO.getRefundReason()))
                .refundRemarks(nullCheckForString(masterSalesReportDTO.getRefundRemarks()))
                .closeSyncDate(closeSyncDate)
                .closeSyncTime(closeSyncTime)
                .transactionDate(transactionDate)
                .transactionTime(transactionTime)
                .transactionId(masterSalesReportDTO.getTransactionId())
                .carrierCode(masterSalesReportDTO.getCarrierCode())
                .flightNumber(masterSalesReportDTO.getFlightNumber())
                .origin(masterSalesReportDTO.getOrigin())
                .destination(masterSalesReportDTO.getDestination())
                .flightDate(masterSalesReportDTO.getFlightDate())
                .departureTime(masterSalesReportDTO.getDepartureTime())
                .arrivalTime(masterSalesReportDTO.getArrivalTime())
                .originCountry(StringUtils.trimWhitespace(masterSalesReportDTO.getOriginCountry()))
                .destinationCountry(StringUtils.trimWhitespace(masterSalesReportDTO.getDestinationCountry()))
                .aircraftRegistration(masterSalesReportDTO.getAircraftRegistration())
                .aircraftType(masterSalesReportDTO.getAircraftType())
                .productBrand(masterSalesReportDTO.getProductBrand())
                .productCategory(masterSalesReportDTO.getProductCategory())
                .productType(masterSalesReportDTO.getProductType())
                .productCode(masterSalesReportDTO.getProductCode())
                .promoRefId(masterSalesReportDTO.getPromoRefId())
                .product(masterSalesReportDTO.getProduct())
                .quantity(masterSalesReportDTO.getQuantity())
                .baseCurrency(masterSalesReportDTO.getBaseCurrency())
                .productPrice(masterSalesReportDTO.getProductPrice())
                .rateType("")
                .rate("")
                .grossSales(nullCheckForMonetary(masterSalesReportDTO.getGrossSalesInBigDecimal()))
                .nettSales(nullCheckForMonetary(masterSalesReportDTO.getNettSalesInBigDecimal()))
                .priceOverwrite(nullCheckForMonetary(masterSalesReportDTO.getPriceOverwriteInBigDecimal()))
                .priceVariance(nullCheckForMonetary(masterSalesReportDTO.getPriceVarianceInBigDecimal()))
                .priceOverrideReason(nullCheckForString(masterSalesReportDTO.getPriceOverrideReason()))
                .priceOverrideRemarks(nullCheckForString(masterSalesReportDTO.getPriceOverrideRemarks()))
                .promoType(promoType)
                .promoName(promoName)
                .discountType(discountType)
                .discountRate(nullCheckForObject(masterSalesReportDTO.getDiscountRateInDouble()))
                .discountAmount(nullCheckForMonetary(masterSalesReportDTO.getDiscountAmountInBigDecimal()))
                .isPromoEntry(isPromoEntry)
                .promoRefId(masterSalesReportDTO.getPromoRefId())
                .seatNumber(masterSalesReportDTO.getSeatNumber())
                .cardHolderName("")
                .cardNumber(masterSalesReportDTO.getCardNumber())
                .creditCardType(masterSalesReportDTO.getCreditCardType())
                .referencePGRN(masterSalesReportDTO.getReferencePGRN())
                .authorizationDate(authorizationDate)
                .authorizationTime(authorizationTime)
                .paymentStatus(paymentStatus)
                .card(cardAmount)
                .resubmission(resubmission)
                .rejectCode(rejectCode)
                .deviceName(masterSalesReportDTO.getDeviceName())
                .deviceId(masterSalesReportDTO.getDeviceId())
                .crewId(masterSalesReportDTO.getCrewId())
                .build();
    }
}

package com.jolly.core.batch.processor;

import com.softspace.fasspos.common.batch.dto.FlightSalesReportDTO;
import com.softspace.fasspos.entity.constant.DiscountTypeEnum;
import com.softspace.fasspos.entity.constant.PromoTypeEnum;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FlightSalesReportProcessor extends AbstractItemProcessor implements ItemProcessor<FlightSalesReportDTO, FlightSalesReportDTO> {

    @Override
    public FlightSalesReportDTO process(FlightSalesReportDTO flightSalesReportDTO) throws Exception {

        String carrierCode = Optional.ofNullable(flightSalesReportDTO.getCarrierCode())
                .map(String::trim)
                .orElse("");
        String flightNumber = Optional.ofNullable(flightSalesReportDTO.getFlightNumber())
                .map(String::trim)
                .orElse("");
        String origin = Optional.ofNullable(flightSalesReportDTO.getOrigin())
                .map(String::trim)
                .orElse("");
        String destination = Optional.ofNullable(flightSalesReportDTO.getDestination())
                .map(String::trim)
                .orElse("");
        String countryOfOrigin = Optional.ofNullable(flightSalesReportDTO.getCountryOfOrigin())
                .map(String::trim)
                .orElse("");
        String countryOfDestination = Optional.ofNullable(flightSalesReportDTO.getCountryOfDestination())
                .map(String::trim)
                .orElse("");
        String flightDate = Optional.ofNullable(flightSalesReportDTO.getFlightDate())
                .map(String::trim)
                .orElse("");
        String departureTime = Optional.ofNullable(flightSalesReportDTO.getDepartureTime())
                .map(String::trim)
                .orElse("");
        String productBrand = Optional.ofNullable(flightSalesReportDTO.getProductBrand())
                .map(value -> "\"" + value.trim().replaceAll("\"", "\"\"") + "\"")
                .orElse("");
        String productCategory = Optional.ofNullable(flightSalesReportDTO.getProductCategory())
                .map(value -> "\"" + value.trim().replaceAll("\"", "\"\"") + "\"")
                .orElse("");
        String productType = Optional.ofNullable(flightSalesReportDTO.getProductType())
                .map(value -> "\"" + value.trim().replaceAll("\"", "\"\"") + "\"")
                .orElse("");
        String productCode = Optional.ofNullable(flightSalesReportDTO.getProductCode())
                .map(value -> "\"" + value.trim().replaceAll("\"", "\"\"") + "\"")
                .orElse("");
        String promoRefId = Optional.ofNullable(flightSalesReportDTO.getPromoRefId())
                .map(value -> String.join("|", value.split(",")))
                .orElse("");
        String product = Optional.ofNullable(flightSalesReportDTO.getProduct())
                .map(value -> "\"" + value.trim().replaceAll("\"", "\"\"") + "\"")
                .orElse("");
        String baseCurrency = Optional.ofNullable(flightSalesReportDTO.getBaseCurrency())
                .map(String::trim)
                .orElse("");
        String productPrice = Optional.ofNullable(flightSalesReportDTO.getProductPrice())
                .map(String::trim)
                .orElse("");
        String quantity = Optional.ofNullable(flightSalesReportDTO.getQuantity())
                .map(String::trim)
                .orElse("0");
        String promoType = Optional.ofNullable(flightSalesReportDTO.getPromoType())
                .map(value -> Optional.ofNullable(PromoTypeEnum.getTypeEnum(Integer.parseInt(value)))
                        .map(PromoTypeEnum::getDescription)
                        .orElse("-"))
                .orElse("-");
        String promoName = Optional.ofNullable(flightSalesReportDTO.getPromoName())
                .map(String::trim)
                .orElse("-");
        String discountType = Optional.ofNullable(flightSalesReportDTO.getDiscountType())
                .map(value -> Optional.ofNullable(DiscountTypeEnum.getTypeEnum(Integer.parseInt(value)))
                        .map(DiscountTypeEnum::getDescription)
                        .orElse("-"))
                .orElse("-");
        String discountRate = Optional.ofNullable(flightSalesReportDTO.getDiscountRate())
                .map(String::trim)
                .orElse("0");
        String discountAmount = Optional.ofNullable(flightSalesReportDTO.getDiscountAmount())
                .map(String::trim)
                .orElse("0.00");
        String dutyPaid = Optional.ofNullable(flightSalesReportDTO.getDutyPaid())
                .map(String::trim)
                .orElse("");
        String rateType = Optional.ofNullable(flightSalesReportDTO.getRateType())
                .map(String::trim)
                .orElse("");
        String rate = Optional.ofNullable(flightSalesReportDTO.getRate())
                .map(String::trim)
                .orElse("");
        String grossSalesBase = Optional.ofNullable(flightSalesReportDTO.getGrossSalesBase())
                .map(String::trim)
                .orElse("");
        String netSalesBase = Optional.ofNullable(flightSalesReportDTO.getNetSalesBase())
                .map(String::trim)
                .orElse("");
        String billPromoName = Optional.ofNullable(flightSalesReportDTO.getBillPromoName())
                .map(String::trim)
                .orElse("");
        String billDiscountType = Optional.ofNullable(flightSalesReportDTO.getBillDiscountType())
                .map(grouped -> Arrays.stream(grouped.split("\\|"))
                        .map(value -> Optional.ofNullable(DiscountTypeEnum.getTypeEnum(Integer.parseInt(value)))
                                .map(DiscountTypeEnum::getDescription)
                                .orElse(""))
                        .collect(Collectors.joining("|")))
                .orElse("");
        String billDiscountRate = Optional.ofNullable(flightSalesReportDTO.getBillDiscountRate())
                .map(String::trim)
                .orElse("");
        String billDiscountAmount = Optional.ofNullable(flightSalesReportDTO.getBillDiscountAmount())
                .map(String::trim)
                .orElse("");
        String billSubtotal = Optional.ofNullable(flightSalesReportDTO.getBillSubtotal())
                .map(String::trim)
                .orElse("");
        String billGrandTotal = Optional.ofNullable(flightSalesReportDTO.getBillGrandTotal())
                .map(String::trim)
                .orElse("");

        return FlightSalesReportDTO.builder()
                .carrierCode(carrierCode)
                .flightNumber(flightNumber)
                .origin(origin)
                .destination(destination)
                .countryOfOrigin(countryOfOrigin)
                .countryOfDestination(countryOfDestination)
                .flightDate(flightDate)
                .departureTime(departureTime)
                .productBrand(productBrand)
                .productCategory(productCategory)
                .productType(productType)
                .productCode(productCode)
                .promoRefId(promoRefId)
                .product(product)
                .baseCurrency(baseCurrency)
                .productPrice(productPrice)
                .quantity(quantity)
                .promoType(promoType)
                .promoName(promoName)
                .discountType(discountType)
                .discountRate(discountRate)
                .discountAmount(discountAmount)
                .dutyPaid(dutyPaid)
                .rateType(rateType)
                .rate(rate)
                .grossSalesBase(grossSalesBase)
                .netSalesBase(netSalesBase)
                .billPromoName(billPromoName)
                .billDiscountType(billDiscountType)
                .billDiscountRate(billDiscountRate)
                .billDiscountAmount(billDiscountAmount)
                .billSubtotal(billSubtotal)
                .billGrandTotal(billGrandTotal)
                .build();
    }

}

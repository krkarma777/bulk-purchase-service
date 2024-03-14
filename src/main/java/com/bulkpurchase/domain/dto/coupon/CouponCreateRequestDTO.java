package com.bulkpurchase.domain.dto.coupon;

import com.bulkpurchase.domain.enums.CouponType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CouponCreateRequestDTO {

    private CouponType type;

    private Double discount;

    private LocalDateTime validFrom;

    private LocalDateTime validUntil;

    private Double minimumOrderAmount;

    private Integer quantity;

    private String name;

    private String description;

    private Double maxDiscountAmount;
}

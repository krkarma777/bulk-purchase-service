package com.bulkpurchase.web.service.discount;

import com.bulkpurchase.domain.entity.discount.GlobalDiscount;

public interface GlobalDiscountService {

    Double globalDiscount(GlobalDiscount globalDiscount, Double totalprice);
}

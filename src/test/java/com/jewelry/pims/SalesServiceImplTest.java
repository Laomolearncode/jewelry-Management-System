package com.jewelry.pims;

import com.jewelry.pims.common.BusinessException;
import com.jewelry.pims.common.DocumentStatus;
import com.jewelry.pims.domain.BusinessEntities;
import com.jewelry.pims.domain.MasterEntities;
import com.jewelry.pims.mapper.InventoryMapper;
import com.jewelry.pims.mapper.MasterDataMapper;
import com.jewelry.pims.mapper.SalesMapper;
import com.jewelry.pims.service.impl.SalesServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalesServiceImplTest {

    @Mock
    private SalesMapper salesMapper;
    @Mock
    private MasterDataMapper masterDataMapper;
    @Mock
    private InventoryMapper inventoryMapper;

    @InjectMocks
    private SalesServiceImpl salesService;

    @Test
    void shouldRejectSaleWhenStockIsInsufficient() {
        BusinessEntities.SaleOrder order = new BusinessEntities.SaleOrder();
        order.setId(1L);
        order.setOrderNo("SO001");
        order.setStatus(DocumentStatus.DRAFT.name());

        BusinessEntities.SaleOrderItem item = new BusinessEntities.SaleOrderItem();
        item.setId(1L);
        item.setProductId(100L);
        item.setWarehouseId(1L);
        item.setBatchNo("B001");
        item.setCertificateNo("CERT-001");
        item.setQuantity(5);

        MasterEntities.Stock stock = new MasterEntities.Stock();
        stock.setId(1L);
        stock.setQuantity(2);

        when(salesMapper.findOrderById(1L)).thenReturn(order);
        when(salesMapper.listItemsByOrderId(1L)).thenReturn(List.of(item));
        when(masterDataMapper.findStockByKey(1L, 100L, "B001", "CERT-001")).thenReturn(stock);

        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> salesService.approve(1L));

        Assertions.assertEquals("库存不足，商品ID=100", exception.getMessage());
    }
}

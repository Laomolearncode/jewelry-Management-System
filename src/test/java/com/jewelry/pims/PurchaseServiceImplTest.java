package com.jewelry.pims;

import com.jewelry.pims.common.BusinessException;
import com.jewelry.pims.common.DocumentStatus;
import com.jewelry.pims.domain.BusinessEntities;
import com.jewelry.pims.mapper.InventoryMapper;
import com.jewelry.pims.mapper.MasterDataMapper;
import com.jewelry.pims.mapper.PurchaseMapper;
import com.jewelry.pims.service.impl.PurchaseServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceImplTest {

    @Mock
    private PurchaseMapper purchaseMapper;
    @Mock
    private MasterDataMapper masterDataMapper;
    @Mock
    private InventoryMapper inventoryMapper;

    @InjectMocks
    private PurchaseServiceImpl purchaseService;

    @Test
    void shouldRejectRepeatApproveForNonDraftOrder() {
        BusinessEntities.PurchaseOrder order = new BusinessEntities.PurchaseOrder();
        order.setId(1L);
        order.setStatus(DocumentStatus.APPROVED.name());
        when(purchaseMapper.findOrderById(1L)).thenReturn(order);

        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> purchaseService.approve(1L));

        Assertions.assertEquals("仅草稿单可审核", exception.getMessage());
    }

    @Test
    void shouldRejectRepeatStockInForNonApprovedOrder() {
        BusinessEntities.PurchaseOrder order = new BusinessEntities.PurchaseOrder();
        order.setId(1L);
        order.setStatus(DocumentStatus.STOCKED.name());
        when(purchaseMapper.findOrderById(1L)).thenReturn(order);

        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> purchaseService.stockIn(1L));

        Assertions.assertEquals("仅审核通过的采购单可入库", exception.getMessage());
    }
}

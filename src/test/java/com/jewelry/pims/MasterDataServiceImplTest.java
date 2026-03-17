package com.jewelry.pims;

import com.jewelry.pims.common.BusinessException;
import com.jewelry.pims.dto.master.MasterDtos;
import com.jewelry.pims.mapper.MasterDataMapper;
import com.jewelry.pims.service.impl.MasterDataServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MasterDataServiceImplTest {

    @Mock
    private MasterDataMapper masterDataMapper;

    @InjectMocks
    private MasterDataServiceImpl masterDataService;

    @Test
    void shouldRejectDuplicateProductCode() {
        MasterDtos.ProductRequest request = new MasterDtos.ProductRequest();
        request.setProductCode("P001");
        request.setCertificateNo("CERT-001");
        when(masterDataMapper.countProductByCode("P001")).thenReturn(1);

        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> masterDataService.createProduct(request));

        Assertions.assertEquals("商品编码已存在", exception.getMessage());
    }

    @Test
    void shouldRejectDuplicateCertificateNo() {
        MasterDtos.ProductRequest request = new MasterDtos.ProductRequest();
        request.setProductCode("P002");
        request.setCertificateNo("CERT-001");
        when(masterDataMapper.countProductByCode("P002")).thenReturn(0);
        when(masterDataMapper.countByCertificateNo("CERT-001")).thenReturn(1);

        BusinessException exception = Assertions.assertThrows(BusinessException.class,
                () -> masterDataService.createProduct(request));

        Assertions.assertEquals("证书号已存在", exception.getMessage());
    }
}

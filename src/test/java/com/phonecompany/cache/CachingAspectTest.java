package com.phonecompany.cache;

import com.phonecompany.TestUtil;
import com.phonecompany.dao.interfaces.ProductCategoryDao;
import com.phonecompany.dao.interfaces.ServiceDao;
import com.phonecompany.model.Customer;
import com.phonecompany.model.Service;
import com.phonecompany.model.paging.PagingResult;
import com.phonecompany.service.ServiceServiceImpl;
import com.phonecompany.service.interfaces.*;
import org.javatuples.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.sun.corba.se.impl.util.RepositoryId.cache;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CachingAspectTest {

    private ServiceService serviceService;
    @Autowired
    private ServiceDao serviceDao;
    @Autowired
    private ProductCategoryDao productCategoryDao;
    @Autowired
    private FileService fileService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private StatisticsService<LocalDate, Long> statisticsService;

    @MockBean
    private CustomerService customerService;

    private Pair<String, List<Object>> resultIdentifier;

    @Before
    public void setUp() throws Exception {
        List<Object> methodArguments = Arrays.asList(new Object[]{0, 5, 1});
        String methodNameToBeTested = "getServicesByProductCategoryId";
        resultIdentifier = Pair.with(methodNameToBeTested, methodArguments);
        serviceService = new ServiceServiceImpl(serviceDao, productCategoryDao, fileService,
                customerService, orderService, statisticsService);
    }

    @Test
    public void shouldCacheResults() {
        //given
        PagingResult<Service> pagingResult = serviceService
                .getServicesByProductCategoryId(0, 5, 1);
        Customer sampleRepresentative = TestUtil.getSampleRepresentative();
        when(customerService.getCurrentlyLoggedInUser())
                .thenReturn(sampleRepresentative);

        //when
        @SuppressWarnings("unchecked")
        PagingResult<Service> servicePagingResult = (PagingResult<Service>)
                cache.get(resultIdentifier);
        assertThat(servicePagingResult, is(not(nullValue())));

        //then
    }
}

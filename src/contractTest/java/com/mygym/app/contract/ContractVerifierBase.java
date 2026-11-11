package com.mygym.app.contract;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.mygym.app.controller.BranchCrudController;
import com.mygym.app.models.BranchNameWrapper;
import com.mygym.app.service.BranchCrudService;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

public abstract class ContractVerifierBase {

    @BeforeEach
    void setUp() {
        BranchCrudService service = mock(BranchCrudService.class);
        when(service.getAllBranchNames()).thenReturn(new BranchNameWrapper(Map.of("1", "Andheri West")));
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.standaloneSetup(new BranchCrudController(service)).build());
    }
}

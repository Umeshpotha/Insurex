package com.insurex.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:insurex-test;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
@AutoConfigureMockMvc
@WithMockUser(username = "admin@insurex.com", roles = "ADMIN")
class AdminPageIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rendersAdminPages() throws Exception {
        mockMvc.perform(get("/admin")).andExpect(status().isOk()).andExpect(view().name("adminHome"));
        mockMvc.perform(get("/admin/policies")).andExpect(status().isOk()).andExpect(view().name("policies"));
        mockMvc.perform(get("/admin/claims")).andExpect(status().isOk()).andExpect(view().name("claims"));
        mockMvc.perform(get("/admin/customers")).andExpect(status().isOk()).andExpect(view().name("customers"));
    }
}

package com.springboot.project.test.controller.OrganizeController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URISyntaxException;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class OrganizeControllerGetOrganizeNotExistOrganizeTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/organize").setParameter("id", this.organizeId)
                .build();
        var response = this.testRestTemplate.getForEntity(url, Throwable.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Organize does not exist", response.getBody().getMessage());
    }

    @BeforeEach
    public void beforeEach() {
        var email = Generators.timeBasedReorderedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.createAccount(email);
        this.organizeId = Generators.timeBasedReorderedGenerator().generate().toString();
    }
}

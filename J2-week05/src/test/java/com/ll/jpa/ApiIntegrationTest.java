package com.ll.jpa;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.jpa.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;
import java.util.Map;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class ApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired MemberRepository members;
    String username() { return "u" + UUID.randomUUID().toString().replace("-", "").substring(0,20); }
    MockHttpSession joinAndLogin(String name) throws Exception {
        mvc.perform(post("/api/members").contentType("application/json")
            .content(json.writeValueAsString(Map.of("username",name,"password","password123","nickname","회원"))))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.data.password").doesNotExist());
        return (MockHttpSession) mvc.perform(post("/api/members/login").contentType("application/json")
            .content(json.writeValueAsString(Map.of("username",name,"password","password123"))))
            .andExpect(status().isOk()).andReturn().getRequest().getSession(false);
    }
    @Test void signupDuplicateLoginAndLogout() throws Exception {
        String name=username(); MockHttpSession session=joinAndLogin(name);
        assertThat(members.findByUsername(name).orElseThrow().getPassword()).startsWith("$2a$").isNotEqualTo("password123");
        mvc.perform(post("/api/members").contentType("application/json")
            .content(json.writeValueAsString(Map.of("username",name,"password","password123","nickname","회원"))))
            .andExpect(status().isConflict());
        mvc.perform(post("/api/members/login").contentType("application/json")
            .content(json.writeValueAsString(Map.of("username",name,"password","wrong"))))
            .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/members/me").session(session)).andExpect(status().isOk());
        mvc.perform(post("/api/members/logout").session(session)).andExpect(status().isOk());
        mvc.perform(get("/api/members/me")).andExpect(status().isUnauthorized());
    }
    @Test void articleCrudSearchAndPermission() throws Exception {
        MockHttpSession owner=joinAndLogin(username()), other=joinAndLogin(username());
        String payload=json.writeValueAsString(Map.of("title","안녕 JPA","body","테스트 본문"));
        mvc.perform(post("/api/articles").contentType("application/json").content(payload)).andExpect(status().isUnauthorized());
        String response=mvc.perform(post("/api/articles").session(owner).contentType("application/json").content(payload))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.data.createDate").isNotEmpty())
            .andExpect(jsonPath("$.data.authorUsername").isNotEmpty()).andReturn().getResponse().getContentAsString();
        long id=json.readTree(response).path("data").path("id").asLong();
        mvc.perform(get("/api/articles").param("keyword","안녕")).andExpect(status().isOk())
            .andExpect(jsonPath("$.data[?(@.id == " + id + ")]").isNotEmpty());
        mvc.perform(get("/api/articles/by-ids").param("ids",String.valueOf(id))).andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].id").value(id));
        mvc.perform(get("/api/articles/exact").param("title","안녕 JPA").param("body","테스트 본문"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data[?(@.id == " + id + ")]").isNotEmpty());
        mvc.perform(put("/api/articles/"+id).session(other).contentType("application/json").content(payload))
            .andExpect(status().isForbidden());
        mvc.perform(put("/api/articles/"+id).session(owner).contentType("application/json")
            .content(json.writeValueAsString(Map.of("title","수정 제목","body","수정 내용"))))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.title").value("수정 제목"));
        mvc.perform(get("/api/articles/"+id)).andExpect(jsonPath("$.data.title").value("수정 제목"));
        mvc.perform(delete("/api/articles/"+id).session(other)).andExpect(status().isForbidden());
        mvc.perform(delete("/api/articles/"+id).session(owner)).andExpect(status().isOk());
        mvc.perform(get("/api/articles/"+id)).andExpect(status().isNotFound());
    }
    @Test void surlRedirectCountAndValidation() throws Exception {
        MockHttpSession session=joinAndLogin(username());
        String response=mvc.perform(post("/api/surls").session(session).contentType("application/json")
            .content(json.writeValueAsString(Map.of("body","구글","url","https://www.google.com"))))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.data.count").value(0))
            .andExpect(jsonPath("$.data.authorUsername").isNotEmpty()).andReturn().getResponse().getContentAsString();
        long id=json.readTree(response).path("data").path("id").asLong();
        for (int i=0;i<2;i++) mvc.perform(get("/g/"+id)).andExpect(status().isFound()).andExpect(header().string("Location","https://www.google.com"));
        mvc.perform(get("/api/surls/"+id)).andExpect(status().isOk()).andExpect(jsonPath("$.data.count").value(2));
        mvc.perform(get("/api/surls")).andExpect(status().isOk());
        mvc.perform(get("/g/9223372036854775807")).andExpect(status().isNotFound());
        mvc.perform(post("/api/surls").session(session).contentType("application/json")
            .content(json.writeValueAsString(Map.of("body","실패","url","javascript:alert(1)"))))
            .andExpect(status().isBadRequest());
        mvc.perform(post("/api/articles").session(session).contentType("application/json")
            .content(json.writeValueAsString(Map.of("title","","body","본문"))))
            .andExpect(status().isBadRequest());
    }
}

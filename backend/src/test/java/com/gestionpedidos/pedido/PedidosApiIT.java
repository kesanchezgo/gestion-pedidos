package com.gestionpedidos.pedido;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gestionpedidos.TestcontainersConfiguration;
import com.jayway.jsonpath.JsonPath;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * T011 (FR-007): integración de API contra Postgres real (Testcontainers).
 * Escenarios US1: 201 + total en servidor; 400 sin líneas; 400 cantidad 0.
 * También FR-002: el "total" del cliente se ignora.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class PedidosApiIT {

    @Autowired
    private MockMvc mvc;

    private static final String CREAR_OK = """
            {"clienteNombre":"Ana Pérez","clienteEmail":"ana@ejemplo.com","total":999.99,
             "lineas":[
               {"descripcion":"Cafe en grano","cantidad":2,"precioUnitario":9.90},
               {"descripcion":"Taza","cantidad":1,"precioUnitario":14.50}]}
            """;

    @Test
    void crearPedidoCalculaTotalEnServidor() throws Exception {
        mvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREAR_OK))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(34.30))
                .andExpect(jsonPath("$.estado").value("ABIERTO"))
                .andExpect(jsonPath("$.lineas.length()").value(2));
    }

    @Test
    void sinLineasDevuelve400ConErrorDeCampo() throws Exception {
        mvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteNombre\":\"Ana\",\"clienteEmail\":\"a@b.co\",\"lineas\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.lineas").exists());
    }

    @Test
    void cantidadCeroDevuelve400PorCampo() throws Exception {
        mvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteNombre\":\"Ana\",\"clienteEmail\":\"a@b.co\","
                                + "\"lineas\":[{\"descripcion\":\"X\",\"cantidad\":0,\"precioUnitario\":1.00}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors['lineas[0].cantidad']").exists());
    }

    @Test
    void pedidoInexistenteDevuelve404ProblemDetail() throws Exception {
        mvc.perform(get("/api/pedidos/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso no encontrado"));
    }

    @Test
    void obtenerPedidoCreadoDevuelve200ConLineas() throws Exception {
        long id = crear(CREAR_OK);
        mvc.perform(get("/api/pedidos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.lineas.length()").value(2))
                .andExpect(jsonPath("$.total").value(34.30));
    }

    @Test
    void listadoPaginadoOrdenaCronologicamenteDesc() throws Exception {
        long primero = crear(CREAR_OK);
        long segundo = crear(CREAR_OK.replace("ana@ejemplo.com", "lucia@ejemplo.com"));
        String json = mvc.perform(get("/api/pedidos").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)))
                .andReturn().getResponse().getContentAsString();
        List<Integer> ids = JsonPath.read(json, "$.content[*].id");
        assertTrue(ids.get(0) > ids.get(1), "el listado debe ordenar por creadoEn desc");
        assertTrue(segundo > primero, "sanity: los ids crecen con el tiempo");
    }

    private long crear(String cuerpo) throws Exception {
        String json = mvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(json, "$.id")).longValue();
    }
}

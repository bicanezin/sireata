
package br.edu.utfpr.dv.sireata.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Testes {

    //ORGÃO
    @Test
    void getOrgaoId() {
        Orgao o = new Orgao();
        int id = 7;

        o.setIdOrgao(id);
        int result = o.getIdOrgao();

        assertEquals(id, result);
    }

    //USUÁRIO
    @Test
    void getEmail(){
        Usuario u = new Usuario();
        String email = "teste@utfpr.edu.br";

        u.setEmail(email);
        String result = u.getEmail();

        assertEquals(email, result);
    }

    //PAUTA
    @Test
    void getTitulo(){
        Pauta p = new Pauta();
        String titulo = "Organização do financeiro";

        p.setTitulo(titulo);
        String result = p.getTitulo();

        assertEquals(titulo, result);
    }
}

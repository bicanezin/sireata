package br.edu.utfpr.dv.sireata.dao;

import br.edu.utfpr.dv.sireata.model.Pauta;

import java.sql.*;

public class PautaDAO extends CommonMethods<Pauta> {
    public String buscarPorIdQuery() {
        return ("SELECT * FROM pautas WHERE idPauta = ?");
    }

    public String listarPorAtaQuery(int idAta) {
        return ("SELECT * FROM pautas WHERE idAta=" + idAta + " ORDER BY ordem");
    }

    public String excluirQuery(int id) {
        return ("DELETE FROM pautas WHERE idPauta=" + id);
    }

    public String listarTodosQuery(boolean apenasAtivos) {
        return null;
    }

    public String listarPorDepartamentoQuery(int idDepartamento) {
        return null;
    }

    public String listarParaCriacaoAtaQuery(int idDepartamento, int idUsuario) {
        return null;
    }

    public String listarParaConsultaAtasQuery(int idDepartamento, int idUsuario) {
        return null;
    }

    @Override
    public int inserir(Pauta pauta) throws SQLException {
        try (Connection conn = ConnectionDAO.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO pautas(idAta, ordem, titulo, descricao) VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setInt(1, pauta.getAta().getIdAta());
            stmt.setInt(2, pauta.getOrdem());
            stmt.setString(3, pauta.getTitulo());
            stmt.setString(4, pauta.getDescricao());

            stmt.execute();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) pauta.setIdPauta(rs.getInt(1));
            }
            return pauta.getIdPauta();
        }
    }

    @Override
    public int atualizar(Pauta pauta) throws SQLException {
        try (Connection conn = ConnectionDAO.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE pautas SET idAta=?, ordem=?, titulo=?, descricao=? WHERE idPauta=?")
        ) {
            stmt.setInt(1, pauta.getAta().getIdAta());
            stmt.setInt(2, pauta.getOrdem());
            stmt.setString(3, pauta.getTitulo());
            stmt.setString(4, pauta.getDescricao());

            stmt.setInt(5, pauta.getIdPauta());
            stmt.execute();

            return pauta.getIdPauta();
        }
    }

    @Override
    public int salvar(Pauta pauta) throws SQLException {
        boolean inserir = (pauta.getIdPauta() == 0);

        return inserir ? inserir(pauta) : atualizar(pauta);
    }

    @Override
    Pauta carregarObjeto(ResultSet rs) throws SQLException {
        Pauta pauta = new Pauta();

        pauta.setIdPauta(rs.getInt("idPauta"));
        pauta.getAta().setIdAta(rs.getInt("idAta"));
        pauta.setOrdem(rs.getInt("ordem"));
        pauta.setTitulo(rs.getString("titulo"));
        pauta.setDescricao(rs.getString("descricao"));

        return pauta;
    }

}

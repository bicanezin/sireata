package br.edu.utfpr.dv.sireata.dao;

import br.edu.utfpr.dv.sireata.model.Anexo;

import java.sql.*;

public class AnexoDAO extends CommonMethods<Anexo> {
    public String buscarPorIdQuery() {
        return ("SELECT anexos.* FROM anexos " +
                "WHERE idAnexo = ?");
    }

    public String listarPorAtaQuery(int idAta) {
        return ("SELECT anexos.* FROM anexos " +
                "WHERE idAta=" + idAta + " ORDER BY anexos.ordem");
    }

    public String excluirQuery(int id) {
        return ("DELETE FROM anexos WHERE idanexo=" + id);
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
    public int inserir(Anexo anexo) throws SQLException {
        try (Connection conn = ConnectionDAO.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO anexos(idAta, ordem, descricao, arquivo) VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setInt(1, anexo.getAta().getIdAta());
            stmt.setInt(2, anexo.getOrdem());
            stmt.setString(3, anexo.getDescricao());
            stmt.setBytes(4, anexo.getArquivo());

            stmt.execute();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) anexo.setIdAnexo(rs.getInt(1));
            }
            return anexo.getIdAnexo();
        }
    }

    @Override
    public int atualizar(Anexo anexo) throws SQLException {
        try (Connection conn = ConnectionDAO.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE anexos SET idAta=?, ordem=?, descricao=?, arquivo=? WHERE idAnexo=?")
        ) {
            stmt.setInt(1, anexo.getAta().getIdAta());
            stmt.setInt(2, anexo.getOrdem());
            stmt.setString(3, anexo.getDescricao());
            stmt.setBytes(4, anexo.getArquivo());

            stmt.setInt(5, anexo.getIdAnexo());
            stmt.execute();

            return anexo.getIdAnexo();
        }
    }

    @Override
    public int salvar(Anexo anexo) throws SQLException {
        boolean inserir = (anexo.getIdAnexo() == 0);

        return inserir ? inserir(anexo) : atualizar(anexo);
    }

    @Override
    Anexo carregarObjeto(ResultSet rs) throws SQLException {
        Anexo anexo = new Anexo();

        anexo.setIdAnexo(rs.getInt("idAnexo"));
        anexo.getAta().setIdAta(rs.getInt("idAta"));
        anexo.setDescricao(rs.getString("descricao"));
        anexo.setOrdem(rs.getInt("ordem"));
        anexo.setArquivo(rs.getBytes("arquivo"));

        return anexo;
    }
}

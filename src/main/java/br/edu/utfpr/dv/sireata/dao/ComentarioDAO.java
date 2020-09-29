package br.edu.utfpr.dv.sireata.dao;

import br.edu.utfpr.dv.sireata.model.Comentario;
import br.edu.utfpr.dv.sireata.model.Comentario.SituacaoComentario;

import java.sql.*;
import java.util.List;

public class ComentarioDAO extends CommonMethods<Comentario> {
    public String buscarPorIdQuery() {
        return ("SELECT * FROM comentarios WHERE idComentario = ?");
    }

    public String listarPorAtaQuery(int idAta) {
        return null;
    }

    public String excluirQuery(int id) {
        return null;
    }

    public String listarTodosQuery(boolean apenasAtivos) {
        return null;
    }

    public String listarPorDepartamentoQuery(int idDepartamento) {
        return null;
    }

    public String listarParaCriacaoAtaQuery(int id, int idUsuario) {
        return null;
    }

    public String listarParaConsultaAtasQuery(int id, int idUsuario) {
        return null;
    }

    public Comentario buscarPorUsuario(int idUsuario, int idPauta) throws SQLException {
        return super.buscar(idUsuario, "SELECT comentarios.*, usuarios.nome AS nomeUsuario FROM comentarios " +
                "INNER JOIN usuarios ON usuarios.idUsuario=comentarios.idUsuario " +
                "WHERE comentarios.idPauta=" + idPauta + " AND comentarios.idUsuario=" + idUsuario);
    }

    public List<Comentario> listarPorPauta(int idPauta) throws SQLException {
        return super.listar("SELECT comentarios.*, usuarios.nome AS nomeUsuario FROM comentarios " +
                "INNER JOIN usuarios ON usuarios.idUsuario=comentarios.idUsuario " +
                "WHERE comentarios.idPauta=" + idPauta + " ORDER BY usuarios.nome");
    }

    @Override
    public int inserir(Comentario comentario) throws SQLException {
        try (Connection conn = ConnectionDAO.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO comentarios(idPauta, idUsuario, situacao, comentarios, situacaoComentarios, motivo) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setInt(1, comentario.getPauta().getIdPauta());
            stmt.setInt(2, comentario.getUsuario().getIdUsuario());
            stmt.setInt(3, comentario.getSituacao().getValue());
            stmt.setString(4, comentario.getComentarios());
            stmt.setInt(5, comentario.getSituacaoComentarios().getValue());
            stmt.setString(6, comentario.getMotivo());

            stmt.execute();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) comentario.setIdComentario(rs.getInt(1));
            }
            return comentario.getIdComentario();
        }
    }

    @Override
    public int atualizar(Comentario comentario) throws SQLException {
        try (Connection conn = ConnectionDAO.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE anexos SET idAta=?, ordem=?, descricao=?, arquivo=? WHERE idAnexo=?")
        ) {
            stmt.setInt(1, comentario.getPauta().getIdPauta());
            stmt.setInt(2, comentario.getUsuario().getIdUsuario());
            stmt.setInt(3, comentario.getSituacao().getValue());
            stmt.setString(4, comentario.getComentarios());
            stmt.setInt(5, comentario.getSituacaoComentarios().getValue());
            stmt.setString(6, comentario.getMotivo());

            stmt.setInt(7, comentario.getIdComentario());
            stmt.execute();

            return comentario.getIdComentario();
        }
    }

    @Override
    public int salvar(Comentario comentario) throws SQLException {
        boolean inserir = (comentario.getIdComentario() == 0);

        return inserir ? inserir(comentario) : atualizar(comentario);
    }

    @Override
    Comentario carregarObjeto(ResultSet rs) throws SQLException {
        Comentario comentario = new Comentario();

        comentario.setIdComentario(rs.getInt("idComentario"));
        comentario.getPauta().setIdPauta(rs.getInt("idPauta"));
        comentario.getUsuario().setIdUsuario(rs.getInt("idUsuario"));
        comentario.getUsuario().setNome(rs.getString("nomeUsuario"));
        comentario.setSituacao(SituacaoComentario.valueOf(rs.getInt("situacao")));
        comentario.setComentarios(rs.getString("comentarios"));
        comentario.setSituacaoComentarios(SituacaoComentario.valueOf(rs.getInt("situacaoComentarios")));
        comentario.setMotivo(rs.getString("motivo"));

        return comentario;
    }

}

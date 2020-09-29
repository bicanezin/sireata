package br.edu.utfpr.dv.sireata.dao;

import br.edu.utfpr.dv.sireata.model.AtaParticipante;

import java.sql.*;

public class AtaParticipanteDAO extends CommonMethods<AtaParticipante> {

    public String buscarPorIdQuery() {
        return ("SELECT ataparticipantes.*, usuarios.nome AS nomeParticipante FROM ataparticipantes " +
                "INNER JOIN usuarios ON usuarios.idUsuario=ataparticipantes.idUsuario " +
                "WHERE idAtaParticipante = ?");
    }

    public String listarPorAtaQuery(int idAta) {
        return ("SELECT ataparticipantes.*, usuarios.nome AS nomeParticipante FROM ataparticipantes " +
                "INNER JOIN usuarios ON usuarios.idUsuario=ataparticipantes.idUsuario " +
                "WHERE idAta=" + (idAta) + " ORDER BY usuarios.nome");
    }

    public String excluirQuery(int id) {
        return ("DELETE FROM ataparticipantes WHERE idAtaParticipante=" + id);
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
    public int inserir(AtaParticipante participante) throws SQLException {
        try (Connection conn = ConnectionDAO.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO ataparticipantes(idAta, idUsuario, presente, motivo, designacao, membro) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setInt(1, participante.getAta().getIdAta());
            stmt.setInt(2, participante.getParticipante().getIdUsuario());
            stmt.setInt(3, (participante.isPresente() ? 1 : 0));
            stmt.setString(4, participante.getMotivo());
            stmt.setString(5, participante.getDesignacao());
            stmt.setInt(6, (participante.isMembro() ? 1 : 0));

            stmt.execute();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) participante.setIdAtaParticipante(rs.getInt(1));
            }
            return participante.getIdAtaParticipante();
        }
    }

    @Override
    public int atualizar(AtaParticipante participante) throws SQLException {
        try (Connection conn = ConnectionDAO.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE ataparticipantes SET idAta=?, idUsuario=?, presente=?, motivo=?, designacao=?, membro=? WHERE idAtaParticipante=?")
        ) {
            stmt.setInt(1, participante.getAta().getIdAta());
            stmt.setInt(2, participante.getParticipante().getIdUsuario());
            stmt.setInt(3, (participante.isPresente() ? 1 : 0));
            stmt.setString(4, participante.getMotivo());
            stmt.setString(5, participante.getDesignacao());
            stmt.setInt(6, (participante.isMembro() ? 1 : 0));

            stmt.setInt(7, participante.getIdAtaParticipante());
            stmt.execute();

            return participante.getIdAtaParticipante();
        }
    }

    @Override
    public int salvar(AtaParticipante participante) throws SQLException {
        boolean inserir = (participante.getIdAtaParticipante() == 0);

        return inserir ? inserir(participante) : atualizar(participante);
    }

    @Override
    AtaParticipante carregarObjeto(ResultSet rs) throws SQLException {
        AtaParticipante participante = new AtaParticipante();

        participante.setIdAtaParticipante(rs.getInt("idAtaParticipante"));
        participante.getAta().setIdAta(rs.getInt("idAta"));
        participante.getParticipante().setIdUsuario(rs.getInt("idUsuario"));
        participante.getParticipante().setNome(rs.getString("nomeParticipante"));
        participante.setPresente(rs.getInt("presente") == 1);
        participante.setMotivo(rs.getString("motivo"));
        participante.setDesignacao(rs.getString("designacao"));
        participante.setMembro(rs.getInt("membro") == 1);

        return participante;
    }

}

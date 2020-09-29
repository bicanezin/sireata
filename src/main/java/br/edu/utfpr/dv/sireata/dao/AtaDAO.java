package br.edu.utfpr.dv.sireata.dao;

import br.edu.utfpr.dv.sireata.model.Ata;
import br.edu.utfpr.dv.sireata.model.Ata.TipoAta;
import br.edu.utfpr.dv.sireata.util.DateUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AtaDAO extends CommonMethods<Ata> {
    public String buscarPorIdQuery() {
        return ("SELECT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
                "FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
                "INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
                "INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
                "INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
                "WHERE idAta = ?");
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
        return ("SELECT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
                "FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
                "INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
                "INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
                "INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
                "WHERE atas.publicada=1 AND Orgaos.idDepartamento=" + idDepartamento + " ORDER BY atas.data DESC");
    }

    public String listarParaCriacaoAtaQuery(int idDepartamento, int idUsuario) {
        return null;
    }

    public String listarParaConsultaAtasQuery(int idDepartamento, int idUsuario) {
        return null;
    }

    public Ata buscarPorNumero(int idOrgao, TipoAta tipo, int numero, int ano) throws SQLException {
        ResultSet rs;

        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
                                 "FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
                                 "INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
                                 "INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
                                 "INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
                                 "WHERE atas.publicada = 1 AND atas.idOrgao = ? AND atas.tipo = ? AND atas.numero = ? AND YEAR(atas.data) = ?")) {
            stmt.setInt(1, idOrgao);
            stmt.setInt(2, tipo.getValue());
            stmt.setInt(3, numero);
            stmt.setInt(4, ano);

            rs = stmt.executeQuery();

            return rs.next() ? this.carregarObjeto(rs) : null;
        }
    }

    public Ata buscarPorPauta(int idPauta) throws SQLException {
        ResultSet rs;

        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT DISTINCT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
                                 "FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
                                 "INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
                                 "INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
                                 "INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
                                 "INNER JOIN pautas ON pautas.idAta=atas.idAta " +
                                 "WHERE pautas.idPauta = ?")) {

            stmt.setInt(1, idPauta);

            rs = stmt.executeQuery();

            return rs.next() ? this.carregarObjeto(rs) : null;
        }
    }

    public int buscarProximoNumeroAta(int idOrgao, int ano, TipoAta tipo) throws SQLException {
        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT MAX(numero) AS numero FROM atas WHERE idOrgao = ? AND YEAR(data) = ? AND tipo = ?")) {

            stmt.setInt(1, idOrgao);
            stmt.setInt(2, ano);
            stmt.setInt(3, tipo.getValue());

            ResultSet rs = stmt.executeQuery();

            return rs.next() ? rs.getInt("numero") + 1 : null;
        }
    }

    public List<Ata> listar(int idUsuario, int idCampus, int idDepartamento, int idOrgao, boolean publicadas) throws SQLException {
        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 Statement stmt = conn.createStatement();

                 ResultSet rs = stmt.executeQuery("SELECT DISTINCT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
                         "FROM atas INNER JOIN ataparticipantes ON ataparticipantes.idAta=atas.idAta " +
                         "INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
                         "INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
                         "INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
                         "INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
                         "WHERE ataparticipantes.idUsuario = " + idUsuario +
                         " AND atas.publicada = " + (publicadas ? "1 " : "0 ") +
                         (idCampus > 0 ? " AND departamentos.idCampus = " + idCampus : "") +
                         (idDepartamento > 0 ? " AND departamentos.idDepartamento = " + idDepartamento : "") +
                         (idOrgao > 0 ? " AND atas.idOrgao = " + idOrgao : "") +
                         "ORDER BY atas.data DESC")) {

            List<Ata> list = new ArrayList<>();

            while (rs.next()) {
                list.add(this.carregarObjeto(rs));
            }

            return list;
        }
    }

    public List<Ata> listarPublicadas() throws SQLException {
        return super.listar("SELECT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
                "FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
                "INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
                "INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
                "INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
                "WHERE atas.publicada=1 ORDER BY atas.data DESC");
    }

    public List<Ata> listarPorOrgao(int idOrgao) throws SQLException {
        return super.listar("SELECT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
                "FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
                "INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
                "INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
                "INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
                "WHERE atas.publicada=1 AND atas.idOrgao=" + idOrgao + " ORDER BY atas.data DESC");
    }

    public List<Ata> listarPorCampus(int idCampus) throws SQLException {
        return super.listar("SELECT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
                "FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
                "INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
                "INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
                "INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
                "WHERE atas.publicada=1 AND departamentos.idCampus=" + idCampus + " ORDER BY atas.data DESC");
    }

    public List<Ata> listarNaoPublicadas(int idUsuario) throws SQLException {
        return super.listar("SELECT DISTINCT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
                "FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
                "INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
                "INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
                "INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
                "INNER JOIN ataparticipantes ON ataparticipantes.idAta=atas.idAta " +
                "WHERE atas.publicada=0 AND ataparticipantes.idUsuario=" + idUsuario + " ORDER BY atas.data DESC");
    }

    public List<Ata> listarPorOrgao(int idOrgao, int idUsuario) throws SQLException {
        return super.listar("SELECT DISTINCT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
                "FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
                "INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
                "INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
                "INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
                "INNER JOIN ataparticipantes ON ataparticipantes.idAta=atas.idAta " +
                "WHERE atas.publicada=0 AND ataparticipantes.idUsuario=" + idUsuario + " AND atas.idOrgao=" + idOrgao + " ORDER BY atas.data DESC");
    }

    public List<Ata> listarPorDepartamento(int idDepartamento, int idUsuario) throws SQLException {
        return super.listar("SELECT DISTINCT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
                "FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
                "INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
                "INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
                "INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
                "INNER JOIN ataparticipantes ON ataparticipantes.idAta=atas.idAta " +
                "WHERE atas.publicada=0 AND ataparticipantes.idUsuario=" + idUsuario + " AND Orgaos.idDepartamento=" + idDepartamento + " ORDER BY atas.data DESC");
    }

    public List<Ata> listarPorCampus(int idCampus, int idUsuario) throws SQLException {
        return super.listar("SELECT DISTINCT atas.*, orgaos.nome AS orgao, p.nome AS presidente, s.nome AS secretario " +
                "FROM atas INNER JOIN orgaos ON orgaos.idOrgao=atas.idOrgao " +
                "INNER JOIN departamentos ON departamentos.idDepartamento=orgaos.idDepartamento " +
                "INNER JOIN usuarios p ON p.idUsuario=atas.idPresidente " +
                "INNER JOIN usuarios s ON s.idUsuario=atas.idSecretario " +
                "INNER JOIN ataparticipantes ON ataparticipantes.idAta=atas.idAta " +
                "WHERE atas.publicada=0 AND ataparticipantes.idUsuario=" + idUsuario + " AND departamentos.idCampus=" + idCampus + " ORDER BY atas.data DESC");
    }

    @Override
    public int inserir(Ata ata) throws SQLException {
        try (Connection conn = ConnectionDAO.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO atas(idOrgao, idPresidente, idSecretario, tipo, numero, data, local, localCompleto, dataLimiteComentarios, consideracoesIniciais, audio, documento, publicada, dataPublicacao, aceitarComentarios) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, 0, NULL, 0)", Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setInt(1, ata.getOrgao().getIdOrgao());
            stmt.setInt(2, ata.getPresidente().getIdUsuario());
            stmt.setInt(3, ata.getSecretario().getIdUsuario());
            stmt.setInt(4, ata.getTipo().getValue());
            stmt.setInt(5, ata.getNumero());
            stmt.setTimestamp(6, new java.sql.Timestamp(ata.getData().getTime()));
            stmt.setString(7, ata.getLocal());
            stmt.setString(8, ata.getLocalCompleto());
            stmt.setDate(9, new java.sql.Date(ata.getDataLimiteComentarios().getTime()));
            stmt.setString(10, ata.getConsideracoesIniciais());

            if (ata.getAudio() == null) {
                stmt.setNull(11, Types.BINARY);
            } else {
                stmt.setBytes(11, ata.getAudio());
            }

            stmt.execute();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) ata.setIdAta(rs.getInt(1));
            }
            return ata.getIdAta();
        }
    }

    @Override
    public int atualizar(Ata ata) throws SQLException {
        try (Connection conn = ConnectionDAO.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE atas SET idOrgao=?, idPresidente=?, idSecretario=?, tipo=?, numero=?, data=?, local=?, localCompleto=?, dataLimiteComentarios=?, consideracoesIniciais=?, audio=? WHERE idAta=?")
        ) {
            stmt.setInt(1, ata.getOrgao().getIdOrgao());
            stmt.setInt(2, ata.getPresidente().getIdUsuario());
            stmt.setInt(3, ata.getSecretario().getIdUsuario());
            stmt.setInt(4, ata.getTipo().getValue());
            stmt.setInt(5, ata.getNumero());
            stmt.setTimestamp(6, new java.sql.Timestamp(ata.getData().getTime()));
            stmt.setString(7, ata.getLocal());
            stmt.setString(8, ata.getLocalCompleto());
            stmt.setDate(9, new java.sql.Date(ata.getDataLimiteComentarios().getTime()));
            stmt.setString(10, ata.getConsideracoesIniciais());

            if (ata.getAudio() == null) {
                stmt.setNull(11, Types.BINARY);
            } else {
                stmt.setBytes(11, ata.getAudio());
            }

            stmt.setInt(12, ata.getIdAta());

            stmt.execute();

            return ata.getIdAta();
        }
    }

    @Override
    public int salvar(Ata ata) throws SQLException {
        boolean inserir = (ata.getIdAta() == 0);

        return inserir ? inserir(ata) : atualizar(ata);
    }

    public void publicar(int idAta, byte[] documento) throws SQLException {

        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE atas SET documento=?, dataPublicacao=?, publicada=1, aceitarComentarios=0 WHERE publicada=0 AND idAta=?")) {

            stmt.setBytes(1, documento);
            stmt.setTimestamp(2, new java.sql.Timestamp(DateUtils.getNow().getTime().getTime()));
            stmt.setInt(3, idAta);

            stmt.execute();
        }
    }

    public void liberarComentarios(int idAta) throws SQLException {
        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 Statement stmt = conn.createStatement()) {

            stmt.execute("UPDATE atas SET aceitarComentarios=1 WHERE publicada=0 AND idAta=" + idAta);
        }
    }

    public void bloquearComentarios(int idAta) throws SQLException {
        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 Statement stmt = conn.createStatement()) {
            stmt.execute("UPDATE atas SET aceitarComentarios=0 WHERE idAta=" + idAta);
        }
    }

    @Override
    Ata carregarObjeto(ResultSet rs) throws SQLException {
        Ata ata = new Ata();

        ata.setIdAta(rs.getInt("idAta"));
        ata.getOrgao().setIdOrgao(rs.getInt("idOrgao"));
        ata.getOrgao().setNome(rs.getString("orgao"));
        ata.getPresidente().setIdUsuario(rs.getInt("idPresidente"));
        ata.getPresidente().setNome(rs.getString("presidente"));
        ata.getSecretario().setIdUsuario(rs.getInt("idSecretario"));
        ata.getSecretario().setNome(rs.getString("secretario"));
        ata.setTipo(TipoAta.valueOf(rs.getInt("tipo")));
        ata.setNumero(rs.getInt("numero"));
        ata.setData(rs.getTimestamp("data"));
        ata.setLocal(rs.getString("local"));
        ata.setLocalCompleto(rs.getString("localCompleto"));
        ata.setDataLimiteComentarios(rs.getDate("dataLimiteComentarios"));
        ata.setConsideracoesIniciais(rs.getString("consideracoesIniciais"));
        ata.setAudio(rs.getBytes("audio"));
        ata.setPublicada(rs.getInt("publicada") == 1);
        ata.setAceitarComentarios(rs.getInt("aceitarComentarios") == 1);
        ata.setDataPublicacao(rs.getTimestamp("dataPublicacao"));
        ata.setDocumento(rs.getBytes("documento"));

        return ata;
    }

    public boolean temComentarios(int idAta) throws SQLException {
        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 Statement stmt = conn.createStatement();

                 ResultSet rs = stmt.executeQuery("SELECT COUNT(comentarios.idComentario) AS qtde FROM comentarios " +
                         "INNER JOIN pautas ON pautas.idPauta=comentarios.idPauta " +
                         "WHERE pautas.idAta=" + idAta)) {

            return rs.next() && rs.getInt("qtde") > 0;
        }
    }

    public boolean isPresidenteOuSecretario(int idUsuario, int idAta) throws SQLException {

        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 Statement stmt = conn.createStatement();

                 ResultSet rs = stmt.executeQuery("SELECT atas.idAta FROM atas " +
                         "WHERE idAta=" + idAta + " AND (idPresidente=" + idUsuario + " OR idSecretario=" + idUsuario + ")")) {

            return rs.next();
        }
    }

    public boolean isPresidente(int idUsuario, int idAta) throws SQLException {
        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 Statement stmt = conn.createStatement();

                 ResultSet rs = stmt.executeQuery("SELECT atas.idAta FROM atas " +
                         "WHERE idAta=" + idAta + " AND idPresidente=" + idUsuario)) {

            return rs.next();
        }
    }

    public boolean isPublicada(int idAta) throws SQLException {
        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 Statement stmt = conn.createStatement();

                 ResultSet rs = stmt.executeQuery("SELECT atas.publicada FROM atas " +
                         "WHERE idAta=" + idAta)) {

            return rs.next() && rs.getInt("publicada") == 1;
        }
    }

    public boolean excluir(int idAta) throws SQLException {
        try
                (Connection conn = ConnectionDAO.getInstance().getConnection();
                 conn.setAutoCommit(false);
                 Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM comentarios WHERE idPauta IN (SELECT idPauta FROM pautas WHERE idAta=" + idAta + ")");
            stmt.execute("DELETE FROM pautas WHERE idAta=" + idAta);
            stmt.execute("DELETE FROM ataparticipantes WHERE idAta=" + idAta);
            stmt.execute("DELETE FROM anexos WHERE idAta=" + idAta);
            boolean ret = stmt.execute("DELETE FROM atas WHERE idAta=" + idAta);

            conn.commit();

            return ret;
        } catch (SQLException ex) {
            if (conn != null) {
                conn.rollback();
        }
        throw ex;
    }
}

}

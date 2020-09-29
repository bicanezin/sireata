package br.edu.utfpr.dv.sireata.dao;

import br.edu.utfpr.dv.sireata.model.Comentario;
import br.edu.utfpr.dv.sireata.model.Comentario.SituacaoComentario;

import java.sql.*;
import java.util.ArrayList;
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

	public String listarTodosQuery(boolean apenasAtivos){
		return null;
	}

	public String listarPorDepartamentoQuery(int idDepartamento){
		return null;
	}

	public String listarParaCriacaoAtaQuery(int id, int idUsuario){
		return null;
	}

	public String listarParaConsultaAtasQuery(int id, int idUsuario){
		return null;
	}

	public Comentario buscarPorUsuario(int idUsuario, int idPauta) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT comentarios.*, usuarios.nome AS nomeUsuario FROM comentarios " +
				"INNER JOIN usuarios ON usuarios.idUsuario=comentarios.idUsuario " +
				"WHERE comentarios.idPauta=" + idPauta + " AND comentarios.idUsuario=" + idUsuario);
		
			if(rs.next()){
				return this.carregarObjeto(rs);
			}else{
				return null;
			}
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}
	
	public List<Comentario> listarPorPauta(int idPauta) throws SQLException{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT comentarios.*, usuarios.nome AS nomeUsuario FROM comentarios " +
				"INNER JOIN usuarios ON usuarios.idUsuario=comentarios.idUsuario " +
				"WHERE comentarios.idPauta=" + idPauta + " ORDER BY usuarios.nome");
		
			List<Comentario> list = new ArrayList<>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}

	@Override
	public int salvar(Comentario comentario) throws SQLException{
		boolean insert = (comentario.getIdComentario() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
		
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO comentarios(idPauta, idUsuario, situacao, comentarios, situacaoComentarios, motivo) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE comentarios SET idPauta=?, idUsuario=?, situacao=?, comentarios=?, situacaoComentarios=?, motivo=? WHERE idComentario=?");
			}
			
			stmt.setInt(1, comentario.getPauta().getIdPauta());
			stmt.setInt(2, comentario.getUsuario().getIdUsuario());
			stmt.setInt(3, comentario.getSituacao().getValue());
			stmt.setString(4, comentario.getComentarios());
			stmt.setInt(5, comentario.getSituacaoComentarios().getValue());
			stmt.setString(6, comentario.getMotivo());
			
			if(!insert){
				stmt.setInt(7, comentario.getIdComentario());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					comentario.setIdComentario(rs.getInt(1));
				}
			}
			
			return comentario.getIdComentario();
		}finally{
			if((rs != null) && !rs.isClosed())
				rs.close();
			if((stmt != null) && !stmt.isClosed())
				stmt.close();
			if((conn != null) && !conn.isClosed())
				conn.close();
		}
	}

	@Override
	Comentario carregarObjeto(ResultSet rs) throws SQLException{
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

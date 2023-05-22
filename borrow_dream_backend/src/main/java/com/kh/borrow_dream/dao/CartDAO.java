package com.kh.borrow_dream.dao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.unit.DataUnit;

import com.kh.borrow_dream.common.Common;
import com.kh.borrow_dream.vo.CartVO;

public class CartDAO {
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private PreparedStatement pStmt = null;

    public List<CartVO> cartList(String id) {
        List<CartVO> list = new ArrayList<>();
        try {
            conn = Common.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM 장바구니 WHERE BK_CUS = '" + id + "'";
            
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                int bno = rs.getInt("BK_NUM");
                int pno = rs.getInt("BK_AT_NUM");
                String name = rs.getString("BK_NAME");
                int price = rs.getInt("BK_PRICE");
                Date date = rs.getDate("BK_DATE");
                Date borrow1 = rs.getDate("BK_BORROW");
                Date borrow2 = rs.getDate("BK_RETURN");
                int dayCnt = rs.getInt("BK_TOTALDAY");
                int total = rs.getInt("BK_TOTAL");
                int quantity = rs.getInt("BK_QU");
                String userId = rs.getString("BK_CUS");
                String imgPath = rs.getString("BK_IMG");

                CartVO vo = new CartVO();
                vo.setBk_bno(bno);
                vo.setBk_pno(pno);
                vo.setBk_pname(name);
                vo.setBk_price(price);
                vo.setBk_date(date);
                vo.setBorrow1(borrow1);
                vo.setBorrow2(borrow2);
                vo.setDayCnt(dayCnt);
                vo.setTPrice(total);
                vo.setQuantity(quantity);
                vo.setId(userId);
                vo.setImg(imgPath);
                list.add(vo);
            }
            Common.close(rs);
            Common.close(stmt);
            Common.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertItem(String id, String pname, int dayCnt, String startDate, String endDate) {
        int result = 0;
        System.out.println(id + " " + pname + " " + dayCnt + " " + startDate + " " + endDate);
        String sql = "INSERT INTO 장바구니 (BK_NUM, BK_AT_NUM, BK_NAME, BK_PRICE, BK_DATE, BK_BORROW, BK_RETURN, BK_TOTALDAY, BK_TOTAL, BK_QU, BK_CUS, BK_IMG)" +
        "SELECT NVL((SELECT MAX(BK_NUM) FROM 장바구니), 0) + 1, P_NO, P_NAME, P_PRICE, SYSDATE, ?, ?, ?, (P_PRICE * ? * 1), 1, ?, P_IMG FROM 상품정보 WHERE P_NAME = ?";
        try {
            conn = Common.getConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, startDate);
            pStmt.setString(2, endDate);
            pStmt.setInt(3, dayCnt);
            pStmt.setInt(4, dayCnt);
            pStmt.setString(5, id);
            pStmt.setString(6, pname);
    
            result = pStmt.executeUpdate();
            System.out.println("리스트 추가 DB 결과 확인 : " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(pStmt);
        Common.close(conn);
    
        if (result == 1) return true;
        else return false;
    }

    public List<CartVO> checkList(String userId, String pname) {
        List<CartVO> list = new ArrayList<>();
        try {
            conn = Common.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM 장바구니 WHERE BK_CUS = '" + userId + "' AND BK_NAME = " + pname;
            
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                int bno = rs.getInt("BK_NUM");
                int pno = rs.getInt("BK_AT_NUM");
                String name = rs.getString("BK_NAME");
                int price = rs.getInt("BK_PRICE");
                Date date = rs.getDate("BK_DATE");
                Date borrow1 = rs.getDate("BK_BORROW");
                Date borrow2 = rs.getDate("BK_RETURN");
                int dayCnt = rs.getInt("BK_TOTALDAY");
                int total = rs.getInt("BK_TOTAL");
                int quantity = rs.getInt("BK_QU");
                String id = rs.getString("BK_CUS");

                CartVO vo = new CartVO();
                vo.setBk_bno(bno);
                vo.setBk_pno(pno);
                vo.setBk_pname(name);
                vo.setBk_price(price);
                vo.setBk_date(date);
                vo.setBorrow1(borrow1);
                vo.setBorrow2(borrow2);
                vo.setDayCnt(dayCnt);
                vo.setTPrice(total);
                vo.setQuantity(quantity);
                vo.setId(id);
                list.add(vo);
            }
            Common.close(rs);
            Common.close(stmt);
            Common.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Integer listCnt(String id) {
        int listCount = 0;
        
        try{
            conn = Common.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT SUM(BK_QU) AS SUMCNT FROM 장바구니 WHERE BK_CUS = '" + id + "'";
            rs = stmt.executeQuery(sql);
            while(rs.next()){
                listCount = rs.getInt(1);
            }
            
        } catch (Exception e){
            e.printStackTrace();
        }
        
        Common.close(rs);
        Common.close(stmt);
        Common.close(conn);
        return listCount;
        
    }
    
    public boolean deleteCartItem(String userId, String pname) {
        int result = 0;
        String sql = "DELETE FROM 장바구니 WHERE BK_CUS = ? AND BK_NAME = ?";
        try {
            conn = Common.getConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, userId);
            pStmt.setString(2, pname);
            result = pStmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(pStmt);
        Common.close(conn);
        if(result == 1) return true;
        else return false;
    }

    public boolean updateQuantity(String userId, String pname, int quantity) {
        int result = 0;
        String sql = "UPDATE 장바구니 SET BK_QU = ?, BK_TOTAL = BK_PRICE * BK_TOTALDAY * ? WHERE BK_CUS = ? AND BK_NAME = ?";
        try {
            conn = Common.getConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, quantity);
            pStmt.setInt(2, quantity);
            pStmt.setString(3, userId);
            pStmt.setString(4, pname);
            result = pStmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(pStmt);
        Common.close(conn);
        if(result == 1) return true;
        else return false;
    }
}
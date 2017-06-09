package com.csp.galanga.cmd.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.csp.galanga.cmd.Command;
import com.csp.galanga.dao.NotaFiscal;
import com.csp.galanga.util.ReadProps;
import com.csp.galanga.util.Warehouse;

public class FindPisCofins implements Command {

	@Override
	public void doIt() {
		final int pLinha = 9;
		final int uLinha = 3300;
		final Timestamp today = new Timestamp(new Date().getTime());
		
		final String sqlPIS = "select c.id from contabilizacao c join lancamento l on l.id = c.lancamento_id join nota_fiscal n on n.id = l.nota_fiscal_id where c.imposto = 1 and n.id = ? ";
		final String sqlCOFINS = "select c.id from contabilizacao c join lancamento l on l.id = c.lancamento_id join nota_fiscal n on n.id = l.nota_fiscal_id where c.imposto = 3 and n.id = ? ";
		final String update = "UPDATE CONTABILIZACAO C SET C.VALOR = ? WHERE C.ID = ?";
		
		final String insertNotaFiscal = "INSERT INTO NOTA_FISCAL (ID, NUM_NOTA_FISCAL, DATA_RECEBIMENTO, VALOR_TOTAL, IS_NACIONAL, DATA_ENTRADA_SISTEMA) VALUES (?,?,?,?,?,?)";
		final String insertLancamento = "INSERT INTO lancamento (id, data_operacao, nota_fiscal_id, nota_fiscal_saida_id, nota_fiscal_saida_num, tipo_operacao, cod_usuario) VALUES (?,?,?,?,?,?,?)";
		final String insertContabilizacao = "INSERT INTO contabilizacao (lancamento_id, imposto, valor, debito, credito) values (?,?,?,?,?)";
		String filePath = ReadProps.FILE.getKey(ReadProps.FILE.getKey("ENVIRONMENT") + ".FILE.EXCEL");
		System.out.println(filePath);
		File excel = new File(filePath);
		Warehouse w = new Warehouse();
		try (Connection c = w.connectCspSys();) {
			try(Statement stmt = c.createStatement()){
				stmt.executeQuery("delete from contabilizacao");
				stmt.executeQuery("delete from lancamento");
				stmt.executeQuery("delete from nota_fiscal");
			}
			
			System.out.println("delete data");
			ArrayList<NotaFiscal> listaNotas = new ArrayList<>();
			FileInputStream input = new FileInputStream(excel);
			XSSFWorkbook wb = new XSSFWorkbook(input);
			XSSFSheet sheet = wb.getSheet("SUSPENSO CSPSYS");
			try (PreparedStatement stmtSelectPis = c.prepareStatement(sqlPIS);
					PreparedStatement stmtCofins = c.prepareStatement(sqlCOFINS);
					PreparedStatement stmtNotaFiscal = c.prepareStatement(insertNotaFiscal);
					PreparedStatement stmtLancamento = c.prepareStatement(insertLancamento);
					PreparedStatement stmtContabilizacao = c.prepareStatement(insertContabilizacao);
					PreparedStatement updateContabilizacao = c.prepareStatement(update);
			) {
				for (int i = pLinha; i <= uLinha; i++) {
					XSSFRow row = sheet.getRow(i);
					XSSFCell cell = row.getCell(5);
					double value = cell.getNumericCellValue();
					if (value > 0.0) {
						Double temp = value;
						int nf_id = temp.intValue();
						
						stmtSelectPis.setInt(1, nf_id);
						stmtCofins.setInt(1, nf_id);

						Double nfNum = row.getCell(4).getNumericCellValue();
						Date nfDate = row.getCell(3).getDateCellValue();
						double nfTotal = row.getCell(7).getNumericCellValue();
						//Ari 98725-7017 
						String tipo = row.getCell(6).getRawValue();
						double pis = row.getCell(8).getNumericCellValue();
						double cofins = row.getCell(9).getNumericCellValue();
						double ipi = row.getCell(10).getNumericCellValue();
						double icms = row.getCell(11).getNumericCellValue();
						double ii = row.getCell(12).getNumericCellValue();
						double afrmm = row.getCell(13).getNumericCellValue();
						
						try (ResultSet rsPis = stmtSelectPis.executeQuery(); 
								ResultSet rsCofins = stmtCofins.executeQuery()) {
							if (rsPis.next()) {
								int contabilizacao_id = rsPis.getInt(1);
								updateContabilizacao.setDouble(1, pis);
								updateContabilizacao.setInt(2, contabilizacao_id);
								updateContabilizacao.executeUpdate();
								// update
							} else {
								NotaFiscal n = new NotaFiscal(tipo);
								n.setId(nf_id);
								n.setNum(String.valueOf(nfNum.intValue()));
								n.setNf_total(nfTotal);
								n.setPis(pis);
								n.setCofins(cofins);
								n.setIpi(ipi);
								n.setIcms(icms);
								n.setIi(ii);
								n.setAfrmm(afrmm);
								n.setDate(nfDate);

								listaNotas.add(n);
								// insert
							}

							if (rsCofins.next()) {
								int contabilizacao_id = rsCofins.getInt(1);
								updateContabilizacao.setDouble(1, cofins);
								updateContabilizacao.setInt(2, contabilizacao_id);
								updateContabilizacao.executeUpdate();
								// update
							} else {
								// insert
							}
						}

					}
				}
								
				System.out.println("UPDATES DONE");
				for(NotaFiscal e: listaNotas){
					System.out.println("id: " + e.getId());
					stmtNotaFiscal.setInt(1, e.getId());
					stmtNotaFiscal.setString(2, e.getNum());
					Calendar cal = GregorianCalendar.getInstance();
					cal.setTime(e.getDate());
					stmtNotaFiscal.setTimestamp(3, new java.sql.Timestamp(cal.getTimeInMillis()));
					stmtNotaFiscal.setDouble(4, e.getNf_total());
					stmtNotaFiscal.setString(5, e.isNacional());
					stmtNotaFiscal.setTimestamp(6, new Timestamp(new Date().getTime()));
					stmtNotaFiscal.executeUpdate();
					
					int lancamento_id = getLancamentoId(c);
					stmtLancamento.setInt(1, lancamento_id);
					stmtLancamento.setTimestamp(2, today);
					stmtLancamento.setInt(3, e.getId());
					stmtLancamento.setInt(4, 0);
					stmtLancamento.setInt(5, 0);
					stmtLancamento.setInt(6, e.getOperacao());
					stmtLancamento.setString(7, System.getProperty("user.name"));
					stmtLancamento.executeUpdate();
					
					ArrayList<Object[]> cont = e.getContabilizacoes();
					
					for(Object[] o: cont){
						Integer codImposto = (Integer) o[0];
						Double valorImposto = (Double) o[1];
						stmtContabilizacao.setInt(1, lancamento_id);
						stmtContabilizacao.setInt(2, codImposto);
						stmtContabilizacao.setDouble(3, valorImposto);
						stmtContabilizacao.setInt(4, 1);
						stmtContabilizacao.setInt(5, 2);
						stmtContabilizacao.executeUpdate();
					}
					
				}
			}
			System.out.println("INSERTS DONE");
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	private int getLancamentoId(Connection conn) throws SQLException {
		String sql = "select LANCAMENTO_SEQ.nextval from dual";
		int lancamento_id = 0;

		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {
			if (rs.next()) {
				lancamento_id = rs.getInt(1);
			}
		}

		if (lancamento_id == 0) {
			throw new SQLException("lancamento_id not retrieved!");
		}
		return lancamento_id;
	}
	
	
	private void getNotasFiscais(ArrayList<Integer> listaIds) {
		/*
		 * select n.*, c.* from contabilizacao c join lancamento l on
		 * c.lancamento_id = l.id join nota_fiscal n on l.nota_fiscal_id = n.id
		 * where n.data_recebimento > to_date('31-12-2012', 'dd-MM-yyyy') and
		 * c.lancamento_id in( 155 ,216 ,258 ,256 ,296 ,610 ,611 ,608 ,609 ,614
		 * ,615 ,612 ,613 ,618 ,619 ,616 ,617 ,622 ,623 ,620 ,621 ,627 ,626 ,625
		 * ,624 ,631 ,630 ,629 ,628 ,635 ,634 ,633 ,632 ,639 ,638 ,637 ,636 ,580
		 * ,581 ,582 ,583 ,584 ,585 ,586 ,587 ,588 ,589 ,590 ,591 ,593 ,592 ,595
		 * ,594 ,597 ,596 ,599 ,598 ,601 ,600 ,603 ,602 ,605 ,604 ,607 ,606 ,687
		 * ,686 ,685 ,684 ,673 ,672 ,702 ,703 ,700 ,701 ,698 ,699 ,696 ,697 ,694
		 * ,695 ,692 ,693 ,690 ,691 ,688 ,689 ,653 ,652 ,655 ,654 ,649 ,648 ,651
		 * ,650 ,645 ,644 ,647 ,646 ,641 ,640 ,643 ,642 ,668 ,669 ,670 ,671 ,664
		 * ,665 ,666 ,667 ,660 ,661 ,662 ,663 ,656 ,657 ,658 ,659 ,747 ,746 ,745
		 * ,744 ,751 ,750 ,749 ,748 ,739 ,738 ,737 ,736 ,743 ,742 ,741 ,740 ,713
		 * ,712 ,715 ,714 ,717 ,716 ,719 ,718 ,705 ,704 ,707 ,706 ,709 ,708 ,711
		 * ,710 ,728 ,729 ,730 ,731 ,732 ,733 ,734 ,735 ,720 ,721 ,722 ,723 ,724
		 * ,725 ,726 ,727 ,1236 ,1237 ,1238 ,1239 ,1234 ,1235 ,1244 ,1245 ,1246
		 * ,1247 ,1240 ,1241 ,1242 ,1243 ,1255 ,1254 ,1253 ,1252 ,1251 ,1250
		 * ,1249 ,1248 ,1257 ,1256 ,1642 ,1643 ,1640 ,1641 ,1646 ,1647 ,1644
		 * ,1645 ,1634 ,1635 ,1632 ,1633 ,1638 ,1639 ,1636 ,1637 ,1659 ,1658
		 * ,1657 ,1656 ,1663 ,1662 ,1661 ,1660 ,1651 ,1650 ,1649 ,1648 ,1655
		 * ,1654 ,1653 ,1652 ,1608 ,1609 ,1610 ,1611 ,1612 ,1613 ,1614 ,1615
		 * ,1600 ,1601 ,1602 ,1603 ,1604 ,1605 ,1606 ,1607 ,1625 ,1624 ,1627
		 * ,1626 ,1629 ,1628 ,1631 ,1630 ,1617 ,1616 ,1619 ,1618 ,1621 ,1620
		 * ,1623 ,1622 ,1599 ,1598 ,1597 ,1596 ,1595 ,1594 ,1763 ,1762 ,1761
		 * ,1760 ,1767 ,1766 ,1765 ,1764 ,1771 ,1770 ,1769 ,1768 ,1775 ,1774
		 * ,1773 ,1772 ,1729 ,1728 ,1731 ,1730 ,1733 ,1732 ,1735 ,1734 ,1737
		 * ,1736 ,1739 ,1738 ,1741 ,1740 ,1743 ,1742 ,1744 ,1745 ,1746 ,1747
		 * ,1748 ,1749 ,1750 ,1751 ,1752 ,1753 ,1754 ,1755 ,1756 ,1757 ,1758
		 * ,1759 ,1703 ,1702 ,1701 ,1700 ,1699 ,1698 ,1697 ,1696 ,1711 ,1710
		 * ,1709 ,1708 ,1707 ,1706 ,1705 ,1704 ,1718 ,1719 ,1716 ,1717 ,1714
		 * ,1715 ,1712 ,1713 ,1726 ,1727 ,1724 ,1725 ,1722 ,1723 ,1720 ,1721
		 * ,1669 ,1668 ,1671 ,1670 ,1665 ,1664 ,1667 ,1666 ,1677 ,1676 ,1679
		 * ,1678 ,1673 ,1672 ,1675 ,1674 ,1684 ,1685 ,1686 ,1687 ,1680 ,1681
		 * ,1682 ,1683 ,1692 ,1693 ,1694 ,1695 ,1688 ,1689 ,1690 ,1691 ,2750
		 * ,2751 ,2748 ,2749 ,2746 ,2747 ,2744 ,2745 ,2742 ,2743 ,2740 ,2741
		 * ,2738 ,2739 ,2736 ,2737 ,2735 ,2734 ,2733 ,2732 ,2808 ,2809 ,2802
		 * ,2803 ,2800 ,2801 ,2806 ,2807 ,2804 ,2805 ,2795 ,2794 ,2793 ,2792
		 * ,2799 ,2798 ,2797 ,2796 ,2787 ,2786 ,2785 ,2784 ,2791 ,2790 ,2789
		 * ,2788 ,2776 ,2777 ,2778 ,2779 ,2780 ,2781 ,2782 ,2783 ,2768 ,2769
		 * ,2770 ,2771 ,2772 ,2773 ,2774 ,2775 ,2761 ,2760 ,2763 ,2762 ,2765
		 * ,2764 ,2767 ,2766 ,2753 ,2752 ,2755 ,2754 ,2757 ,2756 ,2759 ,2758
		 * ,3051 ,3050 ,3053 ,3052 ,3055 ,3054 ,3064 ,3065 ,3066 ,3067 ,3068
		 * ,3069 ,3070 ,3071 ,3056 ,3057 ,3058 ,3059 ,3060 ,3061 ,3062 ,3063
		 * ,3097 ,3096 ,3099 ,3098 ,3101 ,3100 ,3103 ,3102 ,3089 ,3088 ,3091
		 * ,3090 ,3093 ,3092 ,3095 ,3094 ,3080 ,3081 ,3082 ,3083 ,3084 ,3085
		 * ,3086 ,3087 ,3072 ,3073 ,3074 ,3075 ,3076 ,3077 ,3078 ,3079 ,3975
		 * ,3974 ,3973 ,3972 ,3971 ,3970 ,3969 ,3968 ,3983 ,3982 ,3981 ,3980
		 * ,3979 ,3978 ,3977 ,3976 ,3990 ,3991 ,3988 ,3989 ,3986 ,3987 ,3984
		 * ,3985 ,3996 ,3997 ,3994 ,3995 ,3992 ,3993 ,3944 ,3945 ,3946 ,3947
		 * ,3948 ,3949 ,3950 ,3951 ,3936 ,3937 ,3938 ,3939 ,3940 ,3941 ,3942
		 * ,3943 ,3961 ,3960 ,3963 ,3962 ,3965 ,3964 ,3967 ,3966 ,3953 ,3952
		 * ,3955 ,3954 ,3957 ,3956 ,3959 ,3958 ,3914 ,3915 ,3912 ,3913 ,3918
		 * ,3919 ,3916 ,3917 ,3906 ,3907 ,3904 ,3905 ,3910 ,3911 ,3908 ,3909
		 * ,3931 ,3930 ,3929 ,3928 ,3935 ,3934 ,3933 ,3932 ,3923 ,3922 ,3921
		 * ,3920 ,3927 ,3926 ,3925 ,3924 ,3884 ,3885 ,3886 ,3887 ,3880 ,3881
		 * ,3882 ,3883 ,3876 ,3877 ,3878 ,3879 ,3872 ,3873 ,3874 ,3875 ,3901
		 * ,3900 ,3903 ,3902 ,3897 ,3896 ,3899 ,3898 ,3893 ,3892 ,3895 ,3894
		 * ,3889 ,3888 ,3891 ,3890 ,3854 ,3855 ,3852 ,3853 ,3850 ,3851 ,3848
		 * ,3849 ,3846 ,3847 ,3844 ,3845 ,3871 ,3870 ,3869 ,3868 ,3867 ,3866
		 * ,3865 ,3864 ,3863 ,3862 ,3861 ,3860 ,3859 ,3858 ,3857 ,3856 ) order
		 * by n.id
		 * 
		 * 
		 */
	}

}

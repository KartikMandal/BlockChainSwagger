package com.kcm.garage.fest;


import io.swagger.annotations.Api;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import peerbase.Node;
import peerbase.PeerInfo;
import peerbase.util.SimpleRouter;

import com.kcm.garage.bitcoin.controller.BitPay;
import com.kcm.garage.bitcoin.controller.PayException;
import com.kcm.garage.bitcoin.modle.BTCUnit;
import com.kcm.garage.bitcoin.modle.Invoice;
import com.kcm.garage.bitcoin.modle.InvoiceBuyer;
import com.kcm.garage.block.Block;
import com.kcm.garage.block.BlockData;
import com.kcm.garage.chain.Blockchain;
import com.kcm.garage.miner.Miner;




/**
 * Handles requests for the Employee service.
 */
@Api(value="blockChain", description="blockchain information")
@Controller
public class InsuranceController {
	
	private static final Logger logger = LoggerFactory.getLogger(InsuranceController.class);
	
	public static List<Node> nodes = new ArrayList<Node>();
	public static Map<String,PeerInfo> peerInfoMap = new HashMap<String,PeerInfo>();
	public static Map<Long,PeerInfo> peerInfoMapPort = new HashMap<Long,PeerInfo>();
	public static Map<Long,Node> nodePortMap = new HashMap<Long,Node>();
	
	
	 private static BitPay bitpay;
	 private Invoice basicInvoice;
		private final static double BTC_EPSILON = .000000001;
	    private final static double EPSILON = .001;

	    private static String clientName = "BitPay Java Library Tester";
	    private static String pairingCode;
	    private static String refundInvoiceId = null;
	    private static URI myKeyFile;
	
	
	@PostConstruct
	private void init() throws IOException, InterruptedException, PayException,
			URISyntaxException {
		int port1 = 2002;
		int port2 = 2003;
		PeerInfo peerInfo = new PeerInfo(String.valueOf(port1 - 2000),
				"localhost", port1);
		PeerInfo peerInfo2 = new PeerInfo(String.valueOf(port2 - 2000),
				"localhost", port2);
		peerInfoMap.put(String.valueOf(port1 - 2000), peerInfo);
		peerInfoMap.put(String.valueOf(port2 - 2000), peerInfo2);
		peerInfoMapPort.put(Long.valueOf(port1), peerInfo);
		peerInfoMapPort.put(Long.valueOf(port2), peerInfo2);
		Node node = new Node(port1);
		Node node2 = new Node(port2);
		nodes.add(node);
		nodes.add(node2);
		node.makeServerSocket(port1);
		node2.makeServerSocket(port2);
		node2.addPeer(peerInfo);
		node.addPeer(peerInfo2);
		SimpleRouter simpleRouter = new SimpleRouter(node2);
		node.addRouter(simpleRouter);

		setUpOneTime();
	}

	public static void setUpOneTime() throws InterruptedException, IOException,
			PayException, URISyntaxException {
		boolean dumpOut = false;
		File file = new File("D:\\bitpay_private.key");

		URI fileUri = file.toURI();
		System.out.println("URI:" + fileUri);

		URL fileUrl = file.toURI().toURL();
		System.out.println("URL:" + fileUrl);
		myKeyFile = fileUri;
		clientName += " on " + java.net.InetAddress.getLocalHost();
		bitpay = new BitPay(myKeyFile, clientName, BitPay.BITPAY_TEST_URL);
		if (!bitpay.clientIsAuthorized(BitPay.FACADE_POS)) {

			pairingCode = bitpay.requestClientAuthorization(BitPay.FACADE_POS);
			logger.info("Client is requesting POS facade access. Go to "
					+ BitPay.BITPAY_TEST_URL
					+ " and pair this client with your merchant account using the pairing code: "
					+ pairingCode);
			// dumpOut = true;
			Thread.sleep(10000);
		}

		if (!bitpay.clientIsAuthorized(BitPay.FACADE_MERCHANT)) {
			pairingCode = bitpay
					.requestClientAuthorization(BitPay.FACADE_MERCHANT);
			logger.info("Client is requesting MERCHANT facade access. Go to "
					+ BitPay.BITPAY_TEST_URL
					+ " and pair this client with your merchant account using the pairing code: "
					+ pairingCode);
			// dumpOut = true;
		}

		if (dumpOut) {
			throw new PayException("Error: client is not authorized.");
		}
	}

	@RequestMapping(value = RestURIConstants.ADD_BLOCK, method = RequestMethod.GET)
	public @ResponseBody String addBlockInblockChain(
			@RequestParam("insuranceId") String insuranceId,
			@RequestParam("insuranceTerm") String insuranceTerm,
			@RequestParam("insuranceType") String insuranceType,
			@RequestParam("premiumAmount") Double premiumAmount,
			@RequestParam("maturityDate") String maturityDate,
			@RequestParam("phoneNo") String phoneNo,
			@RequestParam("provider") String provider)
			throws NoSuchAlgorithmException, DigestException {
		logger.info("Start addBlock");
		BlockData data = new BlockData(insuranceId, insuranceTerm,
				insuranceType, premiumAmount, maturityDate, phoneNo, provider);
		Block block = Miner.generateNewBlock(data);
		if (Miner.isNewBlockValid(block))
			Blockchain.pushNewBlock(block);
		return block.getHash();
	}

	@RequestMapping(value = RestURIConstants.GET_LIST_OF_BLOCK_DETAILS, method = RequestMethod.GET)
	public @ResponseBody Block getListOfblock() {
		return Blockchain.getLatestBlock();
	}

	@RequestMapping(value = RestURIConstants.CREATE_TXN_BY_BLOCK_CHAIN_ANY_PEER, method = RequestMethod.GET)
	public @ResponseBody Block createTxnByBlockchainAnyPeer(
			@RequestParam("index") int index) {
		return Blockchain.getBlockchainInfo(index);
	}

	@RequestMapping(value = RestURIConstants.ADD_PEER, method = RequestMethod.GET)
	public @ResponseBody String addPeer(@RequestParam("port") int port)
			throws IOException {
		String peerId = String.valueOf(port - 2000);
		PeerInfo peerInfo = new PeerInfo(peerId, "localhost", port);
		if (peerInfoMap.containsKey(peerId)) {
			throw new RuntimeException("Peer on this port already exists");
		}
		peerInfoMap.put(peerId, peerInfo);
		peerInfoMapPort.put(Long.valueOf(port), peerInfo);
		Node node = new Node(port);
		nodePortMap.put(new Long(port), node);
		node.makeServerSocket(port);
		for (Node nodde : nodes) {
			node.addPeer(peerInfoMapPort.get(Long.valueOf(nodde.getPort())));
		}
		nodes.add(node);
		return peerId;
	}

	@RequestMapping(value = RestURIConstants.GET_LIST_OF_PEER_DETAILS, method = RequestMethod.GET)
	public @ResponseBody String getListOfPeer(@RequestParam("port") int port,
			@RequestParam("messageType") String messageType,
			@RequestParam("messageData") String messageData) {
		Node node = nodePortMap.get(Long.valueOf(port));
		Set<String> peerIdSet = node.getPeerKeys();
		for (String peerId : peerIdSet) {
			PeerInfo peerInfo = peerInfoMap.get(peerId);
			int portt = peerInfo.getPort();
			SimpleRouter simpleRouter = new SimpleRouter(nodePortMap.get(portt));
			node.addRouter(simpleRouter);
			node.connectAndSend(peerInfo, messageType, messageData, false);
			System.out
					.println("Sucessfully broadcasted the message to the peer with peer id : "
							+ peerId
							+ " ,Port: "
							+ peerInfoMap.get(peerId).getPort());
		}
		return "Sucessfully broadcasted the message to all the peers";
	}

	@RequestMapping(value = RestURIConstants.CONVERT_TO_BTC, method = RequestMethod.GET)
	public @ResponseBody Invoice getLedgersByAllPeer(
			@RequestParam("name") String name,
			@RequestParam("email") String email,
			@RequestParam("amount") double amount) {
		
		InvoiceBuyer buyer = new InvoiceBuyer();
		buyer.setName(name);
		buyer.setEmail(email);
		String[] temp;
		String delimiter = "@";
		temp = email.split(delimiter);
		long amt=(long)amount;
		BTCUnit bbb=BTCUnit.getBest(BTCUnit.toSatoshis(amt, BTCUnit.getBest(amt)));
		String convertAmountWithUnit=bbb.toPrettyString(amt);
		String[] courency;
		String delimiter2 = " ";
		courency = convertAmountWithUnit.split(delimiter2);
		Invoice invoice = new Invoice(Double.parseDouble(courency[0].trim()), courency[1]);
		invoice.setBuyer(buyer);
		invoice.setFullNotifications(true);
		invoice.setNotificationEmail(temp[0] + "@merchantemaildomain.com");
		//invoice.setPosData("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");
		invoice.setPosData("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567888");
		try {
			invoice = bitpay.createInvoice(invoice);
		} catch (PayException e) {
			e.printStackTrace();
		}
		return invoice;
	}
	 
	 
	 @RequestMapping(value = RestURIConstants.BROAD_CUST_TXN, method = RequestMethod.GET)
		public @ResponseBody String convertMoney(@RequestParam("amount") double amount) {
		 long amt=(long)amount;
		 BigDecimal am=new BigDecimal(amount);
		// return BTCUnit.getBest(amt);
		 
		return "Before convert your exact money is "+amount + " After Satoshi convert your exact money is ->"+BTCUnit.toSatoshis(am, BTCUnit.getBest(amt))+BTCUnit.getBest(BTCUnit.toSatoshis(am, BTCUnit.getBest(amt)));
		}
	 
	 
	/*
	 Map<Integer, Employee> empData = new HashMap<Integer, Employee>();
	 
	 @RequestMapping(value = EmpRestURIConstants.DUMMY_EMP, method = RequestMethod.GET)
	public @ResponseBody Employee getDummyEmployee() {
		logger.info("Start getDummyEmployee");
		Employee emp = new Employee();
		emp.setId(9999);
		emp.setName("Dummy");
		emp.setCreatedDate(new Date());
		empData.put(9999, emp);
		return emp;
	}
	
	@RequestMapping(value = EmpRestURIConstants.GET_EMP, method = RequestMethod.GET)
	public @ResponseBody Employee getEmployee(@PathVariable("id") int empId) {
		logger.info("Start getEmployee. ID="+empId);
		
		return empData.get(empId);
	}
	
	@RequestMapping(value = EmpRestURIConstants.GET_ALL_EMP, method = RequestMethod.GET)
	public @ResponseBody List<Employee> getAllEmployees() {
		logger.info("Start getAllEmployees.");
		List<Employee> emps = new ArrayList<Employee>();
		Set<Integer> empIdKeys = empData.keySet();
		for(Integer i : empIdKeys){
			emps.add(empData.get(i));
		}
		return emps;
	}
	
	@RequestMapping(value = EmpRestURIConstants.CREATE_EMP, method = RequestMethod.POST)
	public @ResponseBody Employee createEmployee(@RequestBody Employee emp) {
		logger.info("Start createEmployee.");
		emp.setCreatedDate(new Date());
		empData.put(emp.getId(), emp);
		return emp;
	}
	
	@RequestMapping(value = EmpRestURIConstants.DELETE_EMP, method = RequestMethod.PUT)
	public @ResponseBody Employee deleteEmployee(@PathVariable("id") int empId) {
		logger.info("Start deleteEmployee.");
		Employee emp = empData.get(empId);
		empData.remove(empId);
		return emp;
	}*/
	
}

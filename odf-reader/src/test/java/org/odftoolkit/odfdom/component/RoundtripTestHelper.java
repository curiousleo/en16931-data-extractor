/*
 * Copyright 2012 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.odftoolkit.odfdom.component;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import static org.odftoolkit.odfdom.component.OperationConstants.DEBUG_OPERATIONS;
import static org.odftoolkit.odfdom.component.OperationConstants.MAX_TABLE_CELLS;
import static org.odftoolkit.odfdom.component.OperationConstants.MAX_TABLE_COLUMNS;
import static org.odftoolkit.odfdom.component.OperationConstants.MAX_TABLE_ROWS;
import org.odftoolkit.odfdom.doc.LoadSaveTest;
import org.odftoolkit.odfdom.pkg.DefaultErrorHandler;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

/**
 * Loads a document with tables and gathers its operations. Gathered operations
 * will be applied to an empty text document. The changed text document will be
 * saved and reloaded. New gathered operations will be compared with the
 * original ones, expected to be identical!
 *
 * @author svanteschubert
 */
class RoundtripTestHelper {

	static protected Logger LOG = Logger.getLogger(RoundtripTestHelper.class.getName());

	public RoundtripTestHelper() {
	}
	static final String INITIAL_OPS_SUFFIX = "-initial_ops.txt";
	static final String RELOADED_OPS_SUFFIX = "-reloaded_ops.txt";
	static final String OUTPUT_DIRECTORY = "regression-tests" + File.separatorChar;
	static final String HYPEN = "-";
	static final String ODT_SUFFIX = ".odt";
	static final String ODS_SUFFIX = ".ods";
	static final List<String> NO_OPERATIONS = new ArrayList<String>(0);
	static final String NO_METHOD_NAME = "";
	// the smallest possible test document of this ODF type. Edited manually and proofed valid by Apache ODF Validator.
	static final String EMPTY_AS_CAN_BE = "empty_as_can_be";


    private Boolean mHasOdfValidationProblem = Boolean.FALSE;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Creating the output directory for the tests
		File outputDir = ResourceUtilities.newTestOutputFile(OUTPUT_DIRECTORY);
		outputDir.mkdir();
	}

	/**
	 * @param testFileNameTrunc the name of the test file before the suffix
	 * (i.e. '.')
	 * @param testMethodName the name of the test method, required for debug
	 * output
	 */
	protected String roundtripRegressionTest(String testFileNameTrunc, String fileNameSuffix) {
		return roundtripOperationTest(testFileNameTrunc, fileNameSuffix, NO_METHOD_NAME, NO_OPERATIONS, null, true, true, true, null, false);
	}

	/**
	 * @param testFileNameTrunc the name of the test file before the suffix
	 * (i.e. '.')
	 * @param testMethodName the name of the test method, required for debug
	 * output
	 */
	protected String roundtripOnlyToOriginalDocRegressionTextTest(String testFileNameTrunc, String testMethodName, String editOperations) {
		return roundtripOperationTest(testFileNameTrunc, ODT_SUFFIX, testMethodName, NO_OPERATIONS, editOperations, true, true, false, null, false);
	}

	/**
	 * @param testFileNameTrunc the name of the test file before the suffix
	 * (i.e. '.')
	 * @param testMethodName the name of the test method, required for debug
	 * output
	 */
	protected String roundtripOnlyToOriginalDocRegressionSpreadsheetTest(String testFileNameTrunc, String testMethodName, String editOperations) {
		return roundtripOperationTest(testFileNameTrunc, ODS_SUFFIX, testMethodName, NO_OPERATIONS, editOperations, true, true, false, null, false);
	}


	/**
	 * @param testFileNameTrunc the name of the test file before the suffix
	 * (i.e. '.')
	 * @param testMethodName the name of the test method, required for debug
	 * output
	 */
	protected String roundtripOnlyToEmptyDocRegressionTest(String testFileNameTrunc, String fileNameSuffix) {
		return roundtripOperationTest(testFileNameTrunc, fileNameSuffix, NO_METHOD_NAME, NO_OPERATIONS, null, true, false, true, null, false);
	}

	/**
	 * @param testFileNameTrunc the name of the test file before the suffix
	 * (i.e. '.')
	 * @param testMethodName the name of the test method, required for debug
	 * output
	 * @param editOperations a list of operations strings to edit the test
	 * document
	 */
	protected String roundtripRegressionTextTest(String testFileNameTrunc, String testMethodName, List<String> editOperations) {
		return roundtripOperationTest(testFileNameTrunc, ODT_SUFFIX, testMethodName, editOperations, null, true, true, true, null, false);
	}

	/**
	 * @param testFileNameTrunc the name of the test file before the suffix
	 * (i.e. '.')
	 * @param testMethodName the name of the test method, required for debug
	 * output
	 * @param editOperations a list of operations strings to edit the test
	 * document
	 */
	protected String roundtripRegressionSpreadsheetTest(String testFileNameTrunc, String testMethodName, List<String> editOperations) {
		return roundtripOperationTest(testFileNameTrunc, ODS_SUFFIX, testMethodName, editOperations, null, true, true, true, null, false);
	}


	/**
	 * @param testFileNameTrunc the name of the test file before the suffix
	 * (i.e. '.')
	 * @param testMethodName the name of the test method, required for debug
	 * output
	 * @param editOperations a list of operations strings to edit the test
	 * document
	 */
	protected String roundtripRegressionTest(String testFileNameTrunc, String testfileNameSuffix, String testMethodName, List<String> editOperations) {
		return roundtripOperationTest(testFileNameTrunc, testfileNameSuffix, testMethodName, editOperations, null, true, true, true, null, false);
	}

	/**
	 * @param testFileNameTrunc the name of the test file before the suffix
	 * (i.e. '.')
	 * @param testMethodName the name of the test method, required for debug
	 * output
	 * @param editOperations a list of operations strings to edit the test
	 * document
	 */
	protected String importOnlyRegressionTest(String testFileNameTrunc, String testfileNameSuffix, String testMethodName, List<String> editOperations) {
		return roundtripOperationTest(testFileNameTrunc, testfileNameSuffix, testMethodName, editOperations, null, false, true, false, null, false);
	}

	/**
	 * @param testFileNameTrunc the name of the test file before the suffix
	 * (i.e. '.')
	 * @param testMethodName the name of the test method, required for debug
	 * output
	 * @param editOperations a list of operations strings to edit the test
	 * document
	 * @param resourceMap a map of resources given by byte arrays identified by
	 * an ID
	 */
	protected String importOnlyRegressionWithResourcesTest(String testFileNameTrunc, String testfileNameSuffix, String testMethodName, List<String> editOperations, Map<Long, byte[]> resourceMap, boolean debug) {
		return roundtripOperationTest(testFileNameTrunc, testfileNameSuffix, testMethodName, editOperations, null, false, true, true, resourceMap, debug);
	}

	/**
	 * @param testFileNameTrunc the name of the test file before the suffix
	 * (i.e. '.')
	 * @param testMethodName the name of the test method, required for debug
	 * output
	 * @param editOperations a list of operations strings to edit the test
	 * document
	 * @param resourceMap a map of resources given by byte arrays identified by
	 * an ID document
	 */
	protected String roundtripRegressionWithResourcesTest(String testFileNameTrunc, String testMethodName, List<String> editOperations, Map<Long, byte[]> resourceMap, boolean debug) {
		return roundtripOperationTest(testFileNameTrunc, ODT_SUFFIX, testMethodName, editOperations, null, true, true, true, resourceMap, debug);
	}

	/**
	 * @param testFileNameTrunc the name of the test file before the suffix
	 * (i.e. '.')
	 * @param testMethodName the name of the test method, required for debug
	 * output
	 * @param editOperations a string of operations to edit the test document
	 */
	protected String roundtripRegressionTextTest(String testFileNameTrunc, String testMethodName, String editOperations) {
		return roundtripOperationTest(testFileNameTrunc, ODT_SUFFIX, testMethodName, null, editOperations, true, true, true, null, false);
	}

	/**
	 * @param testFileNameTrunc the name of the test file before the suffix
	 * (i.e. '.')
	 * @param testMethodName the name of the test method, required for debug
	 * output
	 * @param editOperations a string of operations to edit the test document
	 */
	protected String roundtripRegressionSpreadsheetTest(String testFileNameTrunc, String testMethodName, String editOperations) {
		return roundtripOperationTest(testFileNameTrunc, ODS_SUFFIX, testMethodName, null, editOperations, true, true, true, null, false);
	}

	/**
	 * @param testFileNameTrunc the name of the test file before the suffix
	 * (i.e. '.')
	 * @param testFileNameSuffix the suffix of the file name including the dot
	 * @param testMethodName the name of the test method, required for debug
	 * output
	 * @param editOperations a string of operations to edit the test document
	 */
	protected String roundtripRegressionTest(String testFileNameTrunc, String testfileNameSuffix, String testMethodName, String editOperations) {
		return roundtripOperationTest(testFileNameTrunc, testfileNameSuffix, testMethodName, null, editOperations, true, true, true, null, false);
	}

	/**
	 * @param testFileNameTrunc the name of the test file before the suffix
	 * (i.e. '.')
	 * @param testFileNameSuffix the suffix of the file name including the dot
	 * (e.g. ".odt")
	 * @param editOperationList a list of operations strings to edit the test
	 * document
	 * @param editOperationString a string of operations to edit the test
	 * document
	 */
	protected String roundtripOperationTest(String testFileNameTrunc, String testfileNameSuffix, String testMethodName, List<String> editOperationList, String editOperationString, boolean doReloadTest, boolean applyEditOpsToOriginalDoc, boolean applyOpsToEmptyDoc, Map<Long, byte[]> resourceMap, boolean operationDebugMode) {

        // Activate a default error handler
        // 2DO Svante:Temporary disabling some noise -
        System.setProperty("org.odftoolkit.odfdom.validation", "false");

        OdfOperationDocument doc = null;
		try {
			//**********LOAD
			FileInputStream fis = new FileInputStream(ResourceUtilities.newTestOutputFile(testFileNameTrunc + testfileNameSuffix));
			Map<String, Object> configuration = new HashMap<String, Object>();
			configuration.put(MAX_TABLE_COLUMNS, 0);
            configuration.put(MAX_TABLE_CELLS, 0);
            configuration.put(MAX_TABLE_ROWS, 0);

            configuration.put(DEBUG_OPERATIONS, operationDebugMode);

			if (resourceMap != null) {
				doc = new OdfOperationDocument(fis, resourceMap, configuration);
			} else {
				doc = new OdfOperationDocument(fis, configuration);
			}
		} catch (Throwable e) {
			Logger.getLogger(LoadSaveTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
		return roundtripOperationTest(doc, testFileNameTrunc, testfileNameSuffix, testMethodName, editOperationList, editOperationString, doReloadTest, applyEditOpsToOriginalDoc, applyOpsToEmptyDoc, resourceMap, operationDebugMode);
	}


        private boolean evaluateValidationProblems(OdfOperationDocument doc, String documentName) throws Exception {
            Boolean hasOdfValidationProblem = this.mHasOdfValidationProblem;
            DefaultErrorHandler eHandler = (DefaultErrorHandler) doc.getPackage().getErrorHandler();
            if(eHandler != null && (eHandler.getErrors() != null || eHandler.getFatalErrors() != null)){
                hasOdfValidationProblem = Boolean.TRUE;
                LOG.severe("*** Validated base document: " + documentName);
                LOG.severe(eHandler.getValidationMessages());
            }
            return hasOdfValidationProblem;
        }


	/**
	 * @param testFileNameTrunc the name of the test file before the suffix
	 * (i.e. '.')
	 * @param testFileNameSuffix the suffix of the file name including the dot
	 * (e.g. ".odt")
	 * @param editOperationList a list of operations strings to edit the test
	 * document
	 * @param editOperationString a string of operations to edit the test
	 * document
	 */
	protected String roundtripOperationTest(OdfOperationDocument doc, String testFileNameTrunc, String testfileNameSuffix, String testMethodName, List<String> editOperationList, String editOperationString, boolean doReloadTest, boolean applyEditOpsToOriginalDoc, boolean applyOpsToEmptyDoc, Map<Long, byte[]> resourceMap, boolean operationDebugMode) {

		String savedDocumentPath = null;
		try {
			//**********LOAD
            // reset
            mHasOdfValidationProblem = Boolean.FALSE;
			long docSize = doc.getContentSize();
			LOG.log(Level.INFO, "\n\n******************************************\nDocument name: {0}{1}", new Object[]{testFileNameTrunc, testfileNameSuffix});
			LOG.log(Level.INFO, "Document size: {0} bytes", docSize);
			Assert.assertTrue(docSize > 0);
			// read the (known) operations from the test document
			JSONObject loadingOps = doc.getOperations();
			Assert.assertNotNull(loadingOps);
//2Much		LOG.log(Level.INFO, "The Operation from document in ASCII are: {0}", JsonOperationNormalizer.asString(loadingOps));

			String initialTestOpsFile = OUTPUT_DIRECTORY + testFileNameTrunc + testfileNameSuffix + HYPEN + testMethodName + INITIAL_OPS_SUFFIX;
			String initialTestOps = JsonOperationNormalizer.asString(loadingOps).replace(",{\"name\"", ",\n{\"name\"");
			ResourceUtilities.saveStringToFile(ResourceUtilities.newTestOutputFile(initialTestOpsFile), initialTestOps);

			File initialRefOpsFile = ResourceUtilities.getReferenceFile(initialTestOpsFile);
			boolean initialComparisonFailure = false;
			if (initialRefOpsFile.exists()) {
				String initialRefOps = ResourceUtilities.loadFileAsString(initialRefOpsFile);
				LOG.log(Level.FINEST, "The original ops from testFile are:{0}", initialRefOps);
				if (!initialTestOps.equals(initialRefOps)) {
					LOG.log(Level.SEVERE, "Ups! The original ops from testFile had been:{0}", initialRefOps);
					LOG.log(Level.SEVERE, "But The new ops from testFile are :{0}", initialTestOps);
					initialComparisonFailure = true;
				}
			}

			//**********APPLY
			// to original document document
			int opCount = 0;
			if (applyEditOpsToOriginalDoc) {
				if (editOperationList != null && !editOperationList.isEmpty()) {
					for (String ops : editOperationList) {
						opCount = doc.applyOperations(ops);
						LOG.log(Level.INFO, "opCount: {0}", opCount);
						if (ops.length() > 0 && !ops.equals("[]") && opCount == 0) {
							Assert.fail("Please verify: No operations had been applied!");
						}
					}
				} else if (editOperationString != null && !editOperationString.isEmpty()) {
					opCount = doc.applyOperations(editOperationString);
					if (editOperationString.length() > 0 && !editOperationString.equals("[]") && opCount == 0) {
						Assert.fail("Please verify: No operations had been applied!");
					}
					LOG.log(Level.INFO, "opCount: {0}", opCount);
				}

			}
			// to complete empty document - apply original doc ops
			String odfOutputFile_new = OUTPUT_DIRECTORY + testFileNameTrunc + testfileNameSuffix + HYPEN + testMethodName + HYPEN + "OUT" + HYPEN + "new" + testfileNameSuffix;
			savedDocumentPath = odfOutputFile_new;
			if (applyOpsToEmptyDoc) {
				OdfOperationDocument emptyDoc = null;
				FileInputStream emptyDocFis = null;
				if(testfileNameSuffix.equals(ODT_SUFFIX)){
					emptyDocFis = new FileInputStream(ResourceUtilities.newTestOutputFile(EMPTY_AS_CAN_BE + ODT_SUFFIX));
				}else{
					emptyDocFis = new FileInputStream(ResourceUtilities.newTestOutputFile(EMPTY_AS_CAN_BE + ODS_SUFFIX));
				}
				Map<String, Object> configuration = new HashMap<String, Object>();
				configuration.put("debugoperations", operationDebugMode);
				if (resourceMap != null) {
					emptyDoc = new OdfOperationDocument(emptyDocFis, resourceMap, configuration);
				} else {
					emptyDoc = new OdfOperationDocument(emptyDocFis, configuration);
				}
				opCount = emptyDoc.applyOperations(loadingOps);
				LOG.log(Level.INFO, "opCount: {0}", opCount);
				opCount = 0;
				if (editOperationList != null && !editOperationList.isEmpty()) {
					for (String ops : editOperationList) {
						opCount = emptyDoc.applyOperations(ops);
						LOG.log(Level.INFO, "opCount: {0}", opCount);
						if (ops.length() > 0 && !ops.equals("[]") && opCount == 0) {
							Assert.fail("Please verify: No operations had been applied!");
						}
					}
				} else if (editOperationString != null && !editOperationString.isEmpty()) {
					opCount = emptyDoc.applyOperations(editOperationString);
					if (editOperationString.length() > 0 && !editOperationString.equals("[]") && opCount == 0) {
						Assert.fail("Please verify: No operations had been applied!");
					}
					LOG.log(Level.INFO, "opCount: {0}", opCount);
				}
				//**********SAVE
				// new document adapted
				File testOutputFile2 = ResourceUtilities.newTestOutputFile(odfOutputFile_new);
				emptyDoc.getDocument().save(testOutputFile2);
                mHasOdfValidationProblem = evaluateValidationProblems(emptyDoc, testFileNameTrunc + testfileNameSuffix);
				emptyDoc.close();
				LOG.log(Level.INFO, "***Saved applied new:\n\t{0}", testOutputFile2.getAbsolutePath());
			}
			String odfOutputFile_org = null;
			if (applyEditOpsToOriginalDoc) {
				// orig document changed
				odfOutputFile_org = OUTPUT_DIRECTORY + testFileNameTrunc + testfileNameSuffix + HYPEN + testMethodName + HYPEN + "OUT" + HYPEN + "org" + testfileNameSuffix;
				savedDocumentPath = odfOutputFile_org;
				File testOutputFile = ResourceUtilities.newTestOutputFile(odfOutputFile_org);
				doc.getDocument().save(testOutputFile);

				LOG.log(Level.INFO, "***Saved applied org:\n\t{0}", testOutputFile.getAbsolutePath());
			}
            mHasOdfValidationProblem = evaluateValidationProblems(doc, testFileNameTrunc + testfileNameSuffix);
			doc.close();
			if (doReloadTest) {
				//**********RELOAD
//				String newOpsReloadedFilePath = OUTPUT_DIRECTORY + testFileNameTrunc + testfileNameSuffix + HYPEN + testMethodName + HYPEN + "new" + RELOADED_OPS_SUFFIX;
//				boolean reloadedComparisonFailure_new = reloadDocument(odfOutputFile_new, newOpsReloadedFilePath, newOpsReloadedFilePath);
//
//				if (applyEditOpsToOriginal && applyEditOpsToEmpty) {
//					String orgOpsReloadedFilePath = OUTPUT_DIRECTORY + testFileNameTrunc + testfileNameSuffix + HYPEN + testMethodName + HYPEN + "org" + RELOADED_OPS_SUFFIX;
//					boolean reloadedComparisonFailure_org = reloadDocument(odfOutputFile_org, orgOpsReloadedFilePath, orgOpsReloadedFilePath);
//					Assert.assertTrue("Verified initial ops: '" + !initialComparisonFailure + "'\n"
//							+ "Verified reloaded ops on new File: '" + !reloadedComparisonFailure_new + "',\n" + "Verified reloaded ops on original File: '" + !reloadedComparisonFailure_org + "',\n", !initialComparisonFailure & !reloadedComparisonFailure_new & !reloadedComparisonFailure_org);
//				} else {
//					Assert.assertTrue("Verified initial ops: '" + !initialComparisonFailure + "'\n"
//							+ "Verified reloaded ops on new File: '" + !reloadedComparisonFailure_new + "',\n", !initialComparisonFailure & !reloadedComparisonFailure_new);
//				}

				if (applyEditOpsToOriginalDoc && applyOpsToEmptyDoc) {
					String newOpsReloadedFilePath = OUTPUT_DIRECTORY + testFileNameTrunc + testfileNameSuffix + HYPEN + testMethodName + HYPEN + "new" + RELOADED_OPS_SUFFIX;
					boolean reloadedComparisonFailure_new = reloadDocument(odfOutputFile_new, newOpsReloadedFilePath, newOpsReloadedFilePath);

					String orgOpsReloadedFilePath = OUTPUT_DIRECTORY + testFileNameTrunc + testfileNameSuffix + HYPEN + testMethodName + HYPEN + "org" + RELOADED_OPS_SUFFIX;
					boolean reloadedComparisonFailure_org = reloadDocument(odfOutputFile_org, orgOpsReloadedFilePath, orgOpsReloadedFilePath);
                    Assert.assertTrue("Verified initial ops: '" + !initialComparisonFailure + "'\n"
							+ "Verified reloaded ops on new File: '" + !reloadedComparisonFailure_new + "',\n" + "Verified reloaded ops on original File: '" + !reloadedComparisonFailure_org + "',\n", !initialComparisonFailure & !reloadedComparisonFailure_new & !reloadedComparisonFailure_org);
				} else if (applyOpsToEmptyDoc) {
					String newOpsReloadedFilePath = OUTPUT_DIRECTORY + testFileNameTrunc + testfileNameSuffix + HYPEN + testMethodName + HYPEN + "new" + RELOADED_OPS_SUFFIX;
					boolean reloadedComparisonFailure_new = reloadDocument(odfOutputFile_new, newOpsReloadedFilePath, newOpsReloadedFilePath);

					Assert.assertTrue("Verified initial ops: '" + !initialComparisonFailure + "'\n"
							+ "Verified reloaded ops on new File: '" + !reloadedComparisonFailure_new + "',\n", !initialComparisonFailure & !reloadedComparisonFailure_new);
				} else if (applyEditOpsToOriginalDoc) {
					String orgOpsReloadedFilePath = OUTPUT_DIRECTORY + testFileNameTrunc + testfileNameSuffix + HYPEN + testMethodName + HYPEN + "org" + RELOADED_OPS_SUFFIX;
					boolean reloadedComparisonFailure_org = reloadDocument(odfOutputFile_org, orgOpsReloadedFilePath, orgOpsReloadedFilePath);
					Assert.assertTrue("Verified initial ops: '" + !initialComparisonFailure + "'\n"
							+ "Verified reloaded ops on org File: '" + !reloadedComparisonFailure_org + "',\n", !initialComparisonFailure & !reloadedComparisonFailure_org);
				}

			}else{
				Assert.assertTrue("Verified initial ops: '" + !initialComparisonFailure + "'\n", !initialComparisonFailure);
			}
            if(mHasOdfValidationProblem){
                Assert.fail("Validation Problems with test document: '" + testFileNameTrunc + testfileNameSuffix + "'");
            }
		} catch (Throwable e) {
			Logger.getLogger(RoundtripTestHelper.class.getName()).log(Level.SEVERE, "Testfile: '" + savedDocumentPath + "'\n"   + e.getMessage(), e);
			Assert.fail("Problems with test document " + testFileNameTrunc + " :\n" + e.getMessage());
		}
		return savedDocumentPath;
	}

	private boolean reloadDocument(String odfDocumentToBeReloaded, String testOpsTextFilePath, String referenceOpsTextFilePath) {
		boolean reloadedComparisonFailure = false;
		try {
			OdfOperationDocument reloadedDoc = new OdfOperationDocument(new FileInputStream(ResourceUtilities.newTestOutputFile(odfDocumentToBeReloaded)));
			// read the (known) operation from the test document
			JSONObject reloadedOps = reloadedDoc.getOperations();
//2Much		LOG.log(Level.INFO, "\n\nThe reloaded ops are:{0}", reloadedOps.toString());

			// FOR REGRESSION TEST REFERENCE CREATION - THE OPERATION OF THE RELOADED DOCUMENT
			ResourceUtilities.saveStringToFile(ResourceUtilities.newTestOutputFile(testOpsTextFilePath), JsonOperationNormalizer.asString(reloadedOps).replace(",{\"name\"", ",\n{\"name\""));
			File referenceReloadedOpsFile = ResourceUtilities.getReferenceFile(referenceOpsTextFilePath);
			if (referenceReloadedOpsFile.exists()) {
				String referenceOpsFromFile = ResourceUtilities.loadFileAsString(referenceReloadedOpsFile);
				LOG.log(Level.FINEST, "The reference ops are:{0}", referenceOpsTextFilePath);
				// Test the known read operations with the above one - using ASCII-compare, e.g. UTF-8 character for list bullets
				if (!JsonOperationNormalizer.asString(reloadedOps).replace(",{\"name\"", ",\n{\"name\"").equals(referenceOpsFromFile)) {
					LOG.log(Level.SEVERE, "The reference ops are:{0}", referenceOpsFromFile);
					reloadedComparisonFailure = true;
				}
			}
            mHasOdfValidationProblem = evaluateValidationProblems(reloadedDoc, odfDocumentToBeReloaded);
			reloadedDoc.close();
		} catch (Exception ex) {
			Logger.getLogger(RoundtripTestHelper.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
		}
		return reloadedComparisonFailure;
	}

	/**
	 * Returns the test method name to differentiate multiple outputs from the
	 * same test file
	 */
	protected String getTestMethodName() {
		StackTraceElement stackTraceElements[] = (new Throwable()).getStackTrace();
		String dummy = stackTraceElements[1].toString();
		String dummy2 = dummy.substring(0, dummy.lastIndexOf('('));
		return dummy2.substring(dummy2.lastIndexOf('.') + 1);
	}
}
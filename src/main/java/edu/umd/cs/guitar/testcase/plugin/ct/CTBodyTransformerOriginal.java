/*	
 *  Copyright (c) 2011. The GREYBOX group at the University of Freiburg, Chair of Software Engineering.
 *  Names of owners of this group may be obtained by sending an e-mail to arlt@informatik.uni-freiburg.de
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *	the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *	conditions:
 * 
 *	The above copyright notice and this permission notice shall be included in all copies or substantial 
 *	portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */

package edu.umd.cs.guitar.testcase.plugin.ct;

import edu.umd.cs.guitar.testcase.plugin.ct.entity.CTDef;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.CTMethod;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.CTUnit;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.CTUse;
import edu.umd.cs.guitar.testcase.plugin.ct.util.Log;
import edu.wmich.cs.carot.util.Olog;
import soot.*;
import soot.jimple.FieldRef;
import soot.jimple.ParameterRef;
import soot.jimple.ThisRef;
import soot.jimple.internal.AbstractInstanceInvokeExpr;
import soot.jimple.internal.AbstractInvokeExpr;
import soot.jimple.internal.JReturnStmt;
import soot.util.Chain;

import java.util.*;

/**
 * @author arlt
 */
public class CTBodyTransformerOriginal extends BodyTransformer {

	/**
	 * Methods
	 */
	private Map<String, CTMethod> methods = new HashMap<String, CTMethod>();

	/**
	 * Units
	 */
	private Set<CTUnit> units = new HashSet<CTUnit>();

	/**
	 * Method defs
	 */
	private Map<CTMethod, Set<CTDef>> methodDefs = new HashMap<CTMethod, Set<CTDef>>();

	/**
	 * Method uses
	 */
	private Map<CTMethod, Set<CTUse>> methodUses = new HashMap<CTMethod, Set<CTUse>>();

	/**
	 * Method field defs
	 */
	private Map<CTMethod, Set<CTDef>> methodFieldDefs = new HashMap<CTMethod, Set<CTDef>>();

	/**
	 * Method field uses
	 */
	private Map<CTMethod, Set<CTUse>> methodFieldUses = new HashMap<CTMethod, Set<CTUse>>();

	/**
	 * Unit defs
	 */
	private Map<CTUnit, Set<CTDef>> unitDefs = new HashMap<CTUnit, Set<CTDef>>();

	/**
	 * Unit uses
	 */
	private Map<CTUnit, Set<CTUse>> unitUses = new HashMap<CTUnit, Set<CTUse>>();

	/**
	 * Considered package e.g. "net.sf.jabref"
	 */
	private String pakkage;

	/**
	 * C-tor
	 */
	public CTBodyTransformerOriginal() {

	}

	@Override
	@SuppressWarnings("rawtypes")
	synchronized protected void internalTransform(Body body, String phase, Map options) {
		// restrict analysis to certain package?
//		if (null != getPackage() && !body.getMethod().getDeclaringClass().getPackageName().startsWith(getPackage()))
//			return;

		// lookup method
		SootMethod method = body.getMethod();
		CTMethod ctMethod = lookupMethod(method.getSignature());
//		Olog.log.info("internalTransform method name: " + method.getName()  + ", @" + this.toString());

		// iterate units
		Chain<Unit> units = body.getUnits();
		for (Unit unit : units) {

			// create unit
			CTUnit ctUnit = new CTUnit();
			ctUnit.setMethod(ctMethod);
			ctUnit.setValue(unit.toString());
			this.units.add(ctUnit);
//
//			// find defs and uses
			findDefs(unit, ctUnit);
			findUses(unit, ctUnit);

			/* no use, because it, var$nameInput etc, is already contained in the method uses. only need add event defs to textFields..


			if(unit.toString().contains("getText") && body.getMethod().toString().contains("$1") ) {

				if (unit instanceof JAssignStmt ) {
					JVirtualInvokeExpr ie = (JVirtualInvokeExpr) ((JAssignStmt)unit).getInvokeExpr();
					Olog.sootAddText.info(ie.toString());
					Olog.sootAddText.info(ie.getBase().toString());
					Unit unit2 = findIdentifyStmt(body, ie.getBase());

					CTUse ctUse = new CTUse();
					ctUse.setUnit(ctUnit);
					ctUse.setValue(((JAssignStmt) unit2).getFieldRef().toString());
					ctUse.setMethodSignature(method.getSignature());
					ctUse.setMethodParameter(0);

					// add use to method and unit
					Olog.sootAddText.info(methodUses.toString());
					addUseToMethod(ctUse, ctUnit.getMethod());
					Olog.sootAddText.info(methodUses.toString());
					addUseToUnit(ctUse, ctUnit);
				} else {
					Olog.sootAddText.info("sth is wrong ..." + Thread.currentThread().getStackTrace().toString());
				}
			}
			*/
		}
	}

	public Unit findIdentifyStmt(Body body, Value v) {
		Olog.sootAddText.info("entering findIdStmt " + v.toString());
		for (Unit unit : body.getUnits()) {
			if(unit.toString().contains(v.toString())) {
				Olog.sootAddText.info("gocha " + unit.toString() + " instance of " + unit.getClass());
				return unit;
			}
		}
		Olog.sootAddText.info("exiting findIdStmt " + v.toString());
		return null;
	}

	/**
	 * Looks up the method for a given signature
	 * 
	 * @param signature
	 *            Siganture
	 * @return Method object
	 */
	protected CTMethod lookupMethod(String signature) {
		if (methods.containsKey(signature)) {
			return methods.get(signature);
		}

		CTMethod method = new CTMethod();
		method.setSignature(signature);
		methods.put(signature, method);
		return method;
	}

	/**
	 * Finds defs in unit
	 * 
	 * @param unit
	 *            Unit
	 */
	protected void findDefs(Unit unit, CTUnit ctUnit) {
		List<ValueBox> defs = unit.getDefBoxes();
		for (ValueBox defBox : defs) {
			Value def = defBox.getValue();

			// create def
			CTDef ctDef = new CTDef();
			ctDef.setUnit(ctUnit);
			ctDef.setValue(def.toString());

			// add def to method and unit
			addDefToMethod(ctDef, ctUnit.getMethod());
			addDefToUnit(ctDef, ctUnit);

			// handle field ref
			if (def instanceof FieldRef) {
				// set field value
				SootFieldRef fieldRef = ((FieldRef) def).getFieldRef();
				ctDef.setFieldValue(fieldRef.getSignature());

				// add field def to method
				addFieldDefToMethod(ctDef, ctUnit.getMethod());
			}
		}
	}

	/**
	 * Finds uses in unit
	 * 
	 * @param unit
	 *            Unit
	 */
	protected void findUses(Unit unit, CTUnit ctUnit) {
		boolean returnStmt = false;
		if (unit instanceof JReturnStmt)
			returnStmt = true;

		List<ValueBox> uses = unit.getUseBoxes();
		for (ValueBox useBox : uses) {
			Value use = useBox.getValue();

			if (use instanceof AbstractInvokeExpr) {
				// add invoke to method
				AbstractInvokeExpr invoke = (AbstractInvokeExpr) use;
				SootMethod method = invoke.getMethod();

				// create use
				CTUse ctUse = new CTUse();
				ctUse.setUnit(ctUnit);
				ctUse.setValue(use.toString());
				ctUse.setMethodSignature(method.getSignature());
				ctUse.setMethodParameter(0);
				ctUse.setReturnStmt(returnStmt);

				// add use to method and unit
				addUseToMethod(ctUse, ctUnit.getMethod());
				addUseToUnit(ctUse, ctUnit);

				// add invoke to method
				ctUnit.getMethod().getInvokes().add(ctUse);

				// add invokedBy to invoked method
				CTMethod invokedMethod = lookupMethod(method.getSignature());
				invokedMethod.getInvokedBy().add(ctUse);

				for (int i = 0; i < use.getUseBoxes().size(); i++) {
					// ignore reference parameter
					if (0 == i && use instanceof AbstractInstanceInvokeExpr)
						continue;

					ValueBox valueBox = (ValueBox) use.getUseBoxes().get(i);
					Value value = valueBox.getValue();

					// create use
					ctUse = new CTUse();
					ctUse.setUnit(ctUnit);
					ctUse.setValue(value.toString());
					ctUse.setMethodSignature(method.getSignature());
					ctUse.setMethodParameter(i + 1);
					ctUse.setReturnStmt(returnStmt);

					// add use to method and unit
					addUseToMethod(ctUse, ctUnit.getMethod());
					addUseToUnit(ctUse, ctUnit);
				}
			} else {
				// ignore this ref
				if (use instanceof ThisRef) {
					continue;
				}

				// create use
				CTUse ctUse = new CTUse();
				ctUse.setUnit(ctUnit);
				ctUse.setValue(use.toString());
				ctUse.setReturnStmt(returnStmt);

				// handle parameters
				if (use instanceof ParameterRef) {
					ParameterRef ref = (ParameterRef) use;
					ctUse.setMethodParameter(ref.getIndex() + 1);
				}
				// handle field refs
				else if (use instanceof FieldRef) {
					SootFieldRef fieldRef = ((FieldRef) use).getFieldRef();
					ctUse.setFieldValue(fieldRef.getSignature());

					// add field use to method
					addFieldUseToMethod(ctUse, ctUnit.getMethod());
				}

				// add use to method and unit
				addUseToMethod(ctUse, ctUnit.getMethod());
				addUseToUnit(ctUse, ctUnit);
			}
		}
	}

	/**
	 * Adds a field def to a method
	 * 
	 * @param def
	 *            Def
	 * @param method
	 *            Method
	 */
	protected void addFieldDefToMethod(CTDef def, CTMethod method) {
		Set<CTDef> fieldDefs = methodFieldDefs.containsKey(method) ? methodFieldDefs
				.get(method) : new HashSet<CTDef>();
		fieldDefs.add(def);
		methodFieldDefs.put(method, fieldDefs);
	}

	/**
	 * Adds a def to a method
	 * 
	 * @param def
	 *            Def
	 * @param method
	 *            Method
	 */
	protected void addDefToMethod(CTDef def, CTMethod method) {
		Set<CTDef> defs = methodDefs.containsKey(method) ? methodDefs
				.get(method) : new HashSet<CTDef>();
		defs.add(def);
		methodDefs.put(method, defs);
	}

	/**
	 * Adds a def to a unit
	 * 
	 * @param def
	 *            Def
	 * @param unit
	 *            Unit
	 */
	protected void addDefToUnit(CTDef def, CTUnit unit) {
		Set<CTDef> defs = unitDefs.containsKey(unit) ? unitDefs.get(unit)
				: new HashSet<CTDef>();
		defs.add(def);
		unitDefs.put(unit, defs);
	}

	/**
	 * Adds a field use to a method
	 * 
	 * @param use
	 *            Use
	 * @param method
	 *            Method
	 */
	protected void addFieldUseToMethod(CTUse use, CTMethod method) {
		Set<CTUse> fieldUses = methodFieldUses.containsKey(method) ? methodFieldUses
				.get(method) : new HashSet<CTUse>();
		fieldUses.add(use);
		methodFieldUses.put(method, fieldUses);
	}

	/**
	 * Adds a use to a method
	 * 
	 * @param use
	 *            Use
	 * @param method
	 *            Method
	 */
	protected void addUseToMethod(CTUse use, CTMethod method) {
		Set<CTUse> uses = methodUses.containsKey(method) ? methodUses
				.get(method) : new HashSet<CTUse>();
		uses.add(use);
		methodUses.put(method, uses);
	}

	/**
	 * Adds a use to a unit
	 * 
	 * @param use
	 *            Use
	 * @param unit
	 *            Unit
	 */
	protected void addUseToUnit(CTUse use, CTUnit unit) {
		Set<CTUse> uses = unitUses.containsKey(unit) ? unitUses.get(unit)
				: new HashSet<CTUse>();
		uses.add(use);
		unitUses.put(unit, uses);
	}

	/**
	 * Returns an internal signature for the given Soot method
	 * 
	 * @param method
	 *            Soot method
	 * @return Method signature
	 */
	public String getMethodSignature(SootMethod method) {
		return method.getSignature();
	}

	/**
	 * Returns a method
	 * 
	 * @param name
	 *            Name of the method
	 * @return Methods
	 */
	public CTMethod getMethod(String name) {
		return methods.containsKey(name) ? methods.get(name) : null;
	}

	/**
	 * Returns all methods
	 * 
	 * @return Methods
	 */
	public Collection<CTMethod> getMethods() {
		return methods.values();
	}

	/**
	 * Returns the field defs per method
	 * 
	 * @param method
	 *            Method
	 * @return Field defs per method
	 */
	public Set<CTDef> getMethodFieldDefs(CTMethod method) {
		return methodFieldDefs.containsKey(method) ? methodFieldDefs
				.get(method) : null;
	}

	/**
	 * Returns the field uses per method
	 * 
	 * @param method
	 *            Method
	 * @return Field uses per method
	 */
	public Set<CTUse> getMethodFieldUses(CTMethod method) {
		return methodFieldUses.containsKey(method) ? methodFieldUses
				.get(method) : null;
	}

	/**
	 * Returns the defs per method
	 * 
	 * @param method
	 *            Method
	 * @return Defs per method
	 */
	public Set<CTDef> getMethodDefs(CTMethod method) {
		return methodDefs.containsKey(method) ? methodDefs.get(method) : null;
	}

	/**
	 * Returns the uses per method
	 * 
	 * @param method
	 *            Method
	 * @return Uses per method
	 */
	public Set<CTUse> getMethodUses(CTMethod method) {
		return methodUses.containsKey(method) ? methodUses.get(method) : null;
	}

	/**
	 * Returns the defs per unit
	 * 
	 * @param unit
	 *            Unit
	 * @return Defs per unit
	 */
	public Set<CTDef> getUnitDefs(CTUnit unit) {
		return unitDefs.containsKey(unit) ? unitDefs.get(unit) : null;
	}

	/**
	 * Returns the uses per unit
	 * 
	 * @param unit
	 *            Unit
	 * @return Uses per unit
	 */
	public Set<CTUse> getUnitUses(CTUnit unit) {
		return unitUses.containsKey(unit) ? unitUses.get(unit) : null;
	}

	/**
	 * Returns the considered package
	 * 
	 * @return Considered package
	 */
	public String getPackage() {
		return pakkage;
	}

	/**
	 * Assigns the considered package
	 * 
	 * @param pakkage
	 *            Considered package
	 */
	public void setPackage(String pakkage) {
		this.pakkage = pakkage;
	}

	/**
	 * Prints statistics of the body transformer
	 */
	public void printStatistics() {
		Log.info("*** Statistics of Body Transformer ***");

		// methods and units
		Log.info(String.format("Methods: %d", methods.size()));
		Log.info(String.format("Units: %d", units.size()));
		Log.info(String.format("Units per Method: %d",
				units.size() / methods.size()));

		// field defs
		int nbrOfFieldDefs = 0;
		for (Set<CTDef> def : methodFieldDefs.values()) {
			nbrOfFieldDefs += def.size();
		}
		Log.info(String.format("Field Defs: %d", nbrOfFieldDefs));

		// field uses
		int nbrOfFieldUses = 0;
		for (Set<CTUse> use : methodFieldUses.values()) {
			nbrOfFieldUses += use.size();
		}
		Log.info(String.format("Field Uses: %d", nbrOfFieldUses));
	}

	public void printData() {
		Olog.log.info("printing CTBodyTransform data:");
		Olog.log.info("=================methodDefs ");
	//	Olog.log.info(methodDefs.toString());
	//	Olog.log.info(methodUses.toString());
		for(Map.Entry<?,?> entry : methodDefs.entrySet()) {
			Object key = entry.getKey();
			Olog.log.info("method: " + key.toString());
			Set<CTDef> value = (Set<CTDef>) entry.getValue();
			for (CTDef e : value)  {
				Olog.log.info("    method defs: " + e.toString());
			}
		}

		Olog.log.info("===================methodUses: ");
		for(Map.Entry<?,?> entry : methodUses.entrySet()) {
			Object key = entry.getKey();
			Olog.log.info("method: " + key.toString());
			Set<CTUse> value = (Set<CTUse>) entry.getValue();
			for (CTUse e : value)  {
				Olog.log.info("    method uses: " + e.toString());
			}
		}
	}

}

package org.saleen.cache.obj;

import org.saleen.rs2.model.definition.GameObjectDefinition;

/**
 * An object definition listener, which is notified when object definitions have
 * been parsed.
 * 
 * @author Graham Edgecombe
 * 
 */
public interface ObjectDefinitionListener {

	/**
	 * Called when an object definition is parsed.
	 * 
	 * @param def
	 *            The definition that was parsed.
	 */
	public void objectDefinitionParsed(GameObjectDefinition def);

}

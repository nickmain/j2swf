package org.epistem.j2swf.swf.tags;

import org.epistem.j2swf.swf.ControlTag;
import org.epistem.j2swf.swf.DefinitionTag;
import org.epistem.j2swf.swf.Timeline;

/**
 * A sprite definition
 *
 * @author nickmain
 */
public class Sprite extends DefinitionTag {

    /**
     * The sprite timeline
     */
    public final Timeline<ControlTag> timeline = new Timeline<ControlTag>();
}

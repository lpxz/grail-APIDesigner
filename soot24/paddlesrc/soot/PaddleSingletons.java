/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/* THIS FILE IS AUTOMATICALLY GENERATED USING soot/make_singletons. DO NOT EDIT! */

package soot;

/** A class to group together all the global variables in Soot. */
public class PaddleSingletons {
    public final class Global {
        private Global() {}
    }
    private Global g = new Global();

    private soot.jimple.paddle.ArrayElement instance_soot_jimple_paddle_ArrayElement;
    public soot.jimple.paddle.ArrayElement soot_jimple_paddle_ArrayElement() {
        if( instance_soot_jimple_paddle_ArrayElement == null ) instance_soot_jimple_paddle_ArrayElement = new soot.jimple.paddle.ArrayElement( g );
        return instance_soot_jimple_paddle_ArrayElement;
    }

    private soot.jimple.paddle.EmptyPointsToSet instance_soot_jimple_paddle_EmptyPointsToSet;
    public soot.jimple.paddle.EmptyPointsToSet soot_jimple_paddle_EmptyPointsToSet() {
        if( instance_soot_jimple_paddle_EmptyPointsToSet == null ) instance_soot_jimple_paddle_EmptyPointsToSet = new soot.jimple.paddle.EmptyPointsToSet( g );
        return instance_soot_jimple_paddle_EmptyPointsToSet;
    }

    private soot.jimple.paddle.PaddleNumberers instance_soot_jimple_paddle_PaddleNumberers;
    public soot.jimple.paddle.PaddleNumberers soot_jimple_paddle_PaddleNumberers() {
        if( instance_soot_jimple_paddle_PaddleNumberers == null ) instance_soot_jimple_paddle_PaddleNumberers = new soot.jimple.paddle.PaddleNumberers( g );
        return instance_soot_jimple_paddle_PaddleNumberers;
    }

    private soot.jimple.paddle.PaddleScene instance_soot_jimple_paddle_PaddleScene;
    public soot.jimple.paddle.PaddleScene soot_jimple_paddle_PaddleScene() {
        if( instance_soot_jimple_paddle_PaddleScene == null ) instance_soot_jimple_paddle_PaddleScene = new soot.jimple.paddle.PaddleScene( g );
        return instance_soot_jimple_paddle_PaddleScene;
    }

    private soot.jimple.paddle.queue.Readers instance_soot_jimple_paddle_queue_Readers;
    public soot.jimple.paddle.queue.Readers soot_jimple_paddle_queue_Readers() {
        if( instance_soot_jimple_paddle_queue_Readers == null ) instance_soot_jimple_paddle_queue_Readers = new soot.jimple.paddle.queue.Readers( g );
        return instance_soot_jimple_paddle_queue_Readers;
    }

    private soot.jimple.paddle.ShadowNumberer instance_soot_jimple_paddle_ShadowNumberer;
    public soot.jimple.paddle.ShadowNumberer soot_jimple_paddle_ShadowNumberer() {
        if( instance_soot_jimple_paddle_ShadowNumberer == null ) instance_soot_jimple_paddle_ShadowNumberer = new soot.jimple.paddle.ShadowNumberer( g );
        return instance_soot_jimple_paddle_ShadowNumberer;
    }

    private soot.jimple.paddle.Results instance_soot_jimple_paddle_Results;
    public soot.jimple.paddle.Results soot_jimple_paddle_Results() {
        if( instance_soot_jimple_paddle_Results == null ) instance_soot_jimple_paddle_Results = new soot.jimple.paddle.Results( g );
        return instance_soot_jimple_paddle_Results;
    }

    private soot.jimple.paddle.AllSharedHybridNodes instance_soot_jimple_paddle_AllSharedHybridNodes;
    public soot.jimple.paddle.AllSharedHybridNodes soot_jimple_paddle_AllSharedHybridNodes() {
        if( instance_soot_jimple_paddle_AllSharedHybridNodes == null ) instance_soot_jimple_paddle_AllSharedHybridNodes = new soot.jimple.paddle.AllSharedHybridNodes( g );
        return instance_soot_jimple_paddle_AllSharedHybridNodes;
    }

}

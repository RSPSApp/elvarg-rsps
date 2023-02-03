package com.runescape.entity.model;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.runescape.Client;
import com.runescape.cache.anim.Frame;
import com.runescape.cache.anim.FrameBase;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.Rasterizer3D;
import com.runescape.engine.impl.MouseHandler;
import com.runescape.entity.Renderable;
import com.runescape.io.Buffer;
import com.runescape.scene.SceneGraph;
import com.runescape.util.ObjectKeyUtil;
import net.runelite.api.Perspective;
import net.runelite.api.hooks.DrawCallbacks;
import net.runelite.api.model.Jarvis;
import net.runelite.api.model.Triangle;
import net.runelite.api.model.Vertex;
import net.runelite.rs.api.RSFrames;
import net.runelite.rs.api.RSModel;

public class Model extends Renderable implements RSModel {

    public boolean DEBUG_MODELS = false;

    public static void clear() {
        modelHeaders = null;
        hasAnEdgeToRestrict = null;
        outOfReach = null;
        vertexScreenY = null;
        vertexScreenZ = null;
        vertexMovedX = null;
        vertexMovedY = null;
        vertexMovedZ = null;
        depth = null;
        faceLists = null;
        anIntArray1673 = null;
        anIntArrayArray1674 = null;
        anIntArray1675 = null;
        anIntArray1676 = null;
        anIntArray1677 = null;
        SINE = null;
        COSINE = null;
        modelColors = null;
        modelLocations = null;
    }

    private Model(int modelId) {

        this.verticesCount = 0;
        this.trianglesCount = 0;
        this.facePriority = 0;
        this.isBoundsCalculated = false;

        byte[] data = modelHeaders[modelId].data;
        if (data[data.length - 1] == -3 && data[data.length - 2] == -1) {
            ModelLoader.decodeType3(this, data);
        } else if (data[data.length - 1] == -2 && data[data.length - 2] == -1) {
            ModelLoader.decodeType2(this, data);
        } else if (data[data.length - 1] == -1 && data[data.length - 2] == -1) {
            ModelLoader.decodeType1(this, data);
        } else {
            ModelLoader.decodeOldFormat(this, data);
        }
    }

    public static void loadModel(final byte[] modelData, final int modelId) {
        if (modelData == null) {
            final ModelHeader modelHeader = modelHeaders[modelId] = new ModelHeader();
            modelHeader.vertexCount = 0;
            modelHeader.triangleCount = 0;
            modelHeader.texturedTriangleCount = 0;
            return;
        }
        final Buffer stream = new Buffer(modelData);
        stream.currentPosition = modelData.length - 18;
        final ModelHeader modelHeader = modelHeaders[modelId] = new ModelHeader();
        modelHeader.data = modelData;
        modelHeader.vertexCount = stream.readUShort();
        modelHeader.triangleCount = stream.readUShort();
        modelHeader.texturedTriangleCount = stream.readUnsignedByte();
        final int useTextures = stream.readUnsignedByte();
        final int useTrianglePriority = stream.readUnsignedByte();
        final int useAlpha = stream.readUnsignedByte();
        final int useTriangleSkins = stream.readUnsignedByte();
        final int useVertexSkins = stream.readUnsignedByte();
        final int dataLengthX = stream.readUShort();
        final int dataLengthY = stream.readUShort();
        final int dataLengthZ = stream.readUShort();
        final int dataLengthTriangle = stream.readUShort();
        int offset = 0;
        modelHeader.vertexDirectionOffset = offset;
        offset += modelHeader.vertexCount;
        modelHeader.triangleTypeOffset = offset;
        offset += modelHeader.triangleCount;
        modelHeader.trianglePriorityOffset = offset;
        if (useTrianglePriority == 255) {
            offset += modelHeader.triangleCount;
        } else {
            modelHeader.trianglePriorityOffset = -useTrianglePriority - 1;
        }
        modelHeader.triangleSkinOffset = offset;
        if (useTriangleSkins == 1) {
            offset += modelHeader.triangleCount;
        } else {
            modelHeader.triangleSkinOffset = -1;
        }
        modelHeader.texturePointerOffset = offset;
        if (useTextures == 1) {
            offset += modelHeader.triangleCount;
        } else {
            modelHeader.texturePointerOffset = -1;
        }
        modelHeader.vertexSkinOffset = offset;
        if (useVertexSkins == 1) {
            offset += modelHeader.vertexCount;
        } else {
            modelHeader.vertexSkinOffset = -1;
        }
        modelHeader.triangleAlphaOffset = offset;
        if (useAlpha == 1) {
            offset += modelHeader.triangleCount;
        } else {
            modelHeader.triangleAlphaOffset = -1;
        }
        modelHeader.triangleDataOffset = offset;
        offset += dataLengthTriangle;
        modelHeader.colourDataOffset = offset;
        offset += modelHeader.triangleCount * 2;
        modelHeader.texturedTriangleOffset = offset;
        offset += modelHeader.texturedTriangleCount * 6;
        modelHeader.dataOffsetX = offset;
        offset += dataLengthX;
        modelHeader.dataOffsetY = offset;
        offset += dataLengthY;
        modelHeader.dataOffsetZ = offset;
        offset += dataLengthZ;
    }

    public static void init() {
        modelHeaders = new ModelHeader[90000];
    }

    public static void resetModel(final int model) {
        modelHeaders[model] = null;
    }

    public static Model getModel(int file) {
        if (modelHeaders == null) {
            return null;
        }

        ModelHeader header = modelHeaders[file];
        if (header == null) {
            Client.instance.resourceProvider.provide(0, file);
            return null;
        } else {
            return new Model(file);
        }
    }

    public static boolean isCached(int file) {
        if (modelHeaders == null)
            return false;

        ModelHeader mdl = modelHeaders[file];
        if (mdl == null) {
            Client.instance.resourceProvider.provide(0,file);
            return false;
        } else {
            return true;
        }
    }

    Model() {
        verticesCount = 0;
        trianglesCount = 0;
        texturesCount = 0;
        facePriority = 0;
        singleTile = true;
        this.isBoundsCalculated = false;
    }

    public Model(int length, Model[] models) {
        try {
            singleTile = false;
            boolean typeFlag = false;
            boolean priorityFlag = false;
            boolean alphaFlag = false;
            boolean tSkinFlag = false;
            boolean colorFlag = false;
            boolean textureFlag = false;
            boolean coordinateFlag = false;
            verticesCount = 0;
            trianglesCount = 0;
            texturesCount = 0;
            facePriority = -1;
            Model build;
            for (int count = 0; count < length; count++) {
                build = models[count];
                if (build != null) {
                    verticesCount += build.verticesCount;
                    trianglesCount += build.trianglesCount;
                    texturesCount += build.texturesCount;
                    typeFlag |= build.drawType != null;
                    alphaFlag |= build.triangleAlpha != null;
                    if (build.renderPriorities != null) {
                        priorityFlag = true;
                    } else {
                        if (facePriority == -1)
                            facePriority = build.facePriority;

                        if (facePriority != build.facePriority)
                            priorityFlag = true;
                    }
                    tSkinFlag |= build.triangleData != null;
                    colorFlag |= build.colors != null;
                    textureFlag |= build.materials != null;
                    coordinateFlag |= build.textures != null;
                }
            }

            verticesX = new int[verticesCount];
            verticesY = new int[verticesCount];
            verticesZ = new int[verticesCount];
            vertexData = new int[verticesCount];
            trianglesX = new int[trianglesCount];
            trianglesY = new int[trianglesCount];
            trianglesZ = new int[trianglesCount];
            if (colorFlag)
                colors = new short[trianglesCount];

            if (typeFlag)
                drawType = new int[trianglesCount];

            if (priorityFlag)
                renderPriorities = new byte[trianglesCount];

            if (alphaFlag)
                triangleAlpha = new byte[trianglesCount];

            if (tSkinFlag)
                triangleData = new int[trianglesCount];

            if (textureFlag)
                materials = new short[trianglesCount];

            if (coordinateFlag)
                textures = new byte[trianglesCount];

            if (texturesCount > 0) {
                textureTypes = new byte[texturesCount];
                texturesX = new short[texturesCount];
                texturesY = new short[texturesCount];
                texturesZ = new short[texturesCount];
            }
            verticesCount = 0;
            trianglesCount = 0;
            texturesCount = 0;
            int textureFace = 0;
            for (int index = 0; index < length; index++) {
                build = models[index];
                if (build != null) {
                    for (int face = 0; face < build.trianglesCount; face++) {
                        if (typeFlag && build.drawType != null)
                            drawType[trianglesCount] = build.drawType[face];

                        if (priorityFlag)
                            if (build.renderPriorities == null)
                                renderPriorities[trianglesCount] = build.facePriority;
                            else
                                renderPriorities[trianglesCount] = build.renderPriorities[face];

                        if (alphaFlag && build.triangleAlpha != null)
                            triangleAlpha[trianglesCount] = build.triangleAlpha[face];

                        if (tSkinFlag && build.triangleData != null)
                            triangleData[trianglesCount] = build.triangleData[face];

                        if (textureFlag) {
                            if (build.materials != null)
                                materials[trianglesCount] = build.materials[face];
                            else
                                materials[trianglesCount] = -1;
                        }
                        if (coordinateFlag) {
                            if (build.textures != null && build.textures[face] != -1) {
                                textures[trianglesCount] = (byte) (build.textures[face] + textureFace);
                            } else {
                                textures[trianglesCount] = -1;
                            }
                        }

                        colors[trianglesCount] = build.colors[face];
                        trianglesX[trianglesCount] = getFirstIdenticalVertexId(build, build.trianglesX[face]);
                        trianglesY[trianglesCount] = getFirstIdenticalVertexId(build, build.trianglesY[face]);
                        trianglesZ[trianglesCount] = getFirstIdenticalVertexId(build, build.trianglesZ[face]);
                        trianglesCount++;
                    }
                    for (int textureEdge = 0; textureEdge < build.texturesCount; textureEdge++) {
                        texturesX[texturesCount] = (short) getFirstIdenticalVertexId(build, build.texturesX[textureEdge]);
                        texturesY[texturesCount] = (short) getFirstIdenticalVertexId(build, build.texturesY[textureEdge]);
                        texturesZ[texturesCount] = (short) getFirstIdenticalVertexId(build, build.texturesZ[textureEdge]);
                        texturesCount++;
                    }
                    textureFace += build.texturesCount;
                }
            }

            if (getFaceTextures() != null)
            {
                int count = getFaceCount();
                float[] uv = new float[count * 6];
                int idx = 0;

                for (int i = 0; i < length; ++i)
                {
                    Model model = models[i];
                    if (model != null)
                    {
                        float[] modelUV = model.getFaceTextureUVCoordinates();

                        if (modelUV != null)
                        {
                            System.arraycopy(modelUV, 0, uv, idx, model.getFaceCount() * 6);
                        }

                        idx += model.getFaceCount() * 6;
                    }
                }

                setFaceTextureUVCoordinates(uv);
            }

            vertexNormals();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Players - graphics in particular
    public Model(Model models[]) {
        int modelCount = 2;
        singleTile = false;
        anInt1620++;
        boolean renderTypeFlag = false;
        boolean priorityFlag = false;
        boolean alphaFlag = false;
        boolean colorFlag = false;
        boolean textureFlag = false;
        boolean coordinateFlag = false;
        verticesCount = 0;
        trianglesCount = 0;
        texturesCount = 0;
        facePriority = -1;
        Model build;
        for (int currentModel = 0; currentModel < modelCount; currentModel++) {
            build = models[currentModel];
            if (build != null) {
                verticesCount += build.verticesCount;
                trianglesCount += build.trianglesCount;
                texturesCount += build.texturesCount;
                renderTypeFlag |= drawType != null;
                if (build.renderPriorities != null) {
                    priorityFlag = true;
                } else {
                    if (facePriority == -1)
                        facePriority = build.facePriority;

                    if (facePriority != build.facePriority)
                        priorityFlag = true;
                }
                alphaFlag |= build.triangleAlpha != null;
                colorFlag |= build.colors != null;
                textureFlag |= build.materials != null;
                coordinateFlag |= build.textures != null;
            }
        }

        verticesX = new int[verticesCount];
        verticesY = new int[verticesCount];
        verticesZ = new int[verticesCount];
        trianglesX = new int[trianglesCount];
        trianglesY = new int[trianglesCount];
        trianglesZ = new int[trianglesCount];
        colorsX = new int[trianglesCount];
        colorsY = new int[trianglesCount];
        colorsZ = new int[trianglesCount];

        if (renderTypeFlag)
            drawType = new int[trianglesCount];

        if (priorityFlag)
            renderPriorities = new byte[trianglesCount];

        if (alphaFlag)
            triangleAlpha = new byte[trianglesCount];

        if (textureFlag)
            materials = new short[trianglesCount];

        if (coordinateFlag)
            textures = new byte[trianglesCount];

        if(texturesCount > 0) {
            textureTypes = new byte[texturesCount];
            texturesX = new short[texturesCount];
            texturesY = new short[texturesCount];
            texturesZ = new short[texturesCount];
        }

        if (colorFlag)
            colors = new short[trianglesCount];

        verticesCount = 0;
        trianglesCount = 0;
        texturesCount = 0;
        for (int currentModel = 0; currentModel < modelCount; currentModel++) {
            build = models[currentModel];
            if (build != null) {
                int vertex = verticesCount;
                for (int point = 0; point < build.verticesCount; point++) {
                    verticesX[verticesCount] = build.verticesX[point];
                    verticesY[verticesCount] = build.verticesY[point];
                    verticesZ[verticesCount] = build.verticesZ[point];
                    verticesCount++;
                }
                for (int face = 0; face < build.trianglesCount; face++) {
                    trianglesX[trianglesCount] = build.trianglesX[face] + vertex;
                    trianglesY[trianglesCount] = build.trianglesY[face] + vertex;
                    trianglesZ[trianglesCount] = build.trianglesZ[face] + vertex;
                    colorsX[trianglesCount] = build.colorsX[face];
                    colorsY[trianglesCount] = build.colorsY[face];
                    colorsZ[trianglesCount] = build.colorsZ[face];

                    if(renderTypeFlag && build.drawType != null) {
                        drawType[trianglesCount] = build.drawType[face];
                    }

                    if (alphaFlag && build.triangleAlpha != null) {
                        triangleAlpha[trianglesCount] = build.triangleAlpha[face];
                    }

                    if (priorityFlag)
                        if (build.renderPriorities == null)
                            renderPriorities[trianglesCount] = build.facePriority;
                        else
                            renderPriorities[trianglesCount] = build.renderPriorities[face];

                    if (colorFlag && build.colors != null)
                        colors[trianglesCount] = build.colors[face];

                    if(textureFlag) {
                        if(build.materials != null) {
                            materials[trianglesCount] = build.materials[face];
                        } else
                            materials[trianglesCount] = -1;
                    }
                    if(coordinateFlag) {
                        if(build.textures != null && build.textures[face] != -1) {
                            textures[trianglesCount] = (byte) (build.textures[face] + texturesCount);

                        } else
                            textures[trianglesCount] = -1;

                    }

                    trianglesCount++;
                }

                for (int texture = 0; texture < build.texturesCount; texture++) {
                    texturesX[texturesCount] = (short) (build.texturesX[texture] + vertex);
                    texturesY[texturesCount] = (short) (build.texturesY[texture] + vertex);
                    texturesZ[texturesCount] = (short) (build.texturesZ[texture] + vertex);
                    texturesCount++;
                }
                texturesCount += build.texturesCount;
            }
        }
        calculateBoundsCylinder();
        resetBounds();
    }

    public Model(boolean colorFlag, boolean alphaFlag, boolean animated, Model model) {
        this(colorFlag, alphaFlag, animated, false, model);
    }

    public Model(boolean colorFlag, boolean alphaFlag, boolean animated, boolean textureFlag, Model model) {
        singleTile = false;
        verticesCount = model.verticesCount;
        trianglesCount = model.trianglesCount;
        texturesCount = model.texturesCount;
        if (animated) {
            verticesX = model.verticesX;
            verticesY = model.verticesY;
            verticesZ = model.verticesZ;
        } else {
            verticesX = new int[verticesCount];
            verticesY = new int[verticesCount];
            verticesZ = new int[verticesCount];
            for (int point = 0; point < verticesCount; point++) {
                verticesX[point] = model.verticesX[point];
                verticesY[point] = model.verticesY[point];
                verticesZ[point] = model.verticesZ[point];
            }

        }
        if (colorFlag) {
            colors = model.colors;
        } else {
            colors = new short[trianglesCount];
            System.arraycopy(model.colors, 0, colors, 0, trianglesCount);
        }

        if (!textureFlag && model.materials != null) {
            materials = new short[trianglesCount];
            System.arraycopy(model.materials, 0, materials, 0, trianglesCount);
        } else {
            materials = model.materials;
        }

        if (alphaFlag) {
            triangleAlpha = model.triangleAlpha;
        } else {
            triangleAlpha = new byte[trianglesCount];
            if (model.triangleAlpha == null) {
                for (int l = 0; l < trianglesCount; l++) {
                    triangleAlpha[l] = 0;
                }

            } else {
                System.arraycopy(model.triangleAlpha, 0, triangleAlpha, 0, trianglesCount);
            }
        }
        vertexData = model.vertexData;
        triangleData = model.triangleData;
        drawType = model.drawType;
        trianglesX = model.trianglesX;
        trianglesY = model.trianglesY;
        trianglesZ = model.trianglesZ;
        renderPriorities = model.renderPriorities;
        facePriority = model.facePriority;
        texturesX = model.texturesX;
        texturesY = model.texturesY;
        texturesZ = model.texturesZ;
        textures = model.textures;
        textureTypes = model.textureTypes;
        normals = model.normals;
        faceNormals = model.faceNormals;
        this.animayaGroups = model.animayaGroups;
        this.animayaScales = model.animayaScales;

        vertexNormalsOffsets = model.vertexNormalsOffsets;
    }

    public Model(boolean resetVertices, boolean resetColors, Model model) {
        singleTile = false;
        verticesCount = model.verticesCount;
        trianglesCount = model.trianglesCount;
        texturesCount = model.texturesCount;

        if (resetVertices) {
            verticesY = new int[verticesCount];
            System.arraycopy(model.verticesY, 0, verticesY, 0, verticesCount);
        } else {
            verticesY = model.verticesY;
        }

        if (resetColors) {
            colorsX = new int[trianglesCount];
            colorsY = new int[trianglesCount];
            colorsZ = new int[trianglesCount];
            for (int face = 0; face < trianglesCount; face++) {
                colorsX[face] = model.colorsX[face];
                colorsY[face] = model.colorsY[face];
                colorsZ[face] = model.colorsZ[face];
            }

            drawType = new int[trianglesCount];
            if (model.drawType == null) {
                for (int face = 0; face < trianglesCount; face++) {
                    drawType[face] = 0;
                }
            } else {
                System.arraycopy(model.drawType, 0, drawType, 0, trianglesCount);
            }


        } else {
            colorsX = model.colorsX;
            colorsY = model.colorsY;
            colorsZ = model.colorsZ;
            drawType = model.drawType;
        }

        verticesX = model.verticesX;
        verticesZ = model.verticesZ;
        colors = model.colors;
        triangleAlpha = model.triangleAlpha;
        renderPriorities = model.renderPriorities;
        facePriority = model.facePriority;
        trianglesX = model.trianglesX;
        trianglesY = model.trianglesY;
        trianglesZ = model.trianglesZ;
        texturesX = model.texturesX;
        texturesY = model.texturesY;
        texturesZ = model.texturesZ;
        super.modelBaseY = model.modelBaseY;
        textures = model.textures;
        materials = model.materials;
        diagonal2DAboveOrigin = model.diagonal2DAboveOrigin;
        diagonal3DAboveOrigin = model.diagonal3DAboveOrigin;
        diagonal3D = model.diagonal3D;
        minX = model.minX;
        maxZ = model.maxZ;
        minZ = model.minZ;
        maxX = model.maxX;

        //New
        vertexNormalsX = model.vertexNormalsX;
        vertexNormalsY = model.vertexNormalsY;
        vertexNormalsZ = model.vertexNormalsZ;
        faceTextureUVCoordinates = model.faceTextureUVCoordinates;
    }

    public void replaceModel(Model model, boolean replaceAlpha) {
        verticesCount = model.verticesCount;
        trianglesCount = model.trianglesCount;
        texturesCount = model.texturesCount;

        if (sharedVerticesX.length < verticesCount) {
            sharedVerticesX = new int[verticesCount + 100];
            sharedVerticesY = new int[verticesCount + 100];
            sharedVerticesZ = new int[verticesCount + 100];
        }

        verticesX = sharedVerticesX;
        verticesY = sharedVerticesY;
        verticesZ = sharedVerticesZ;
        for (int point = 0; point < verticesCount; point++) {
            verticesX[point] = model.verticesX[point];
            verticesY[point] = model.verticesY[point];
            verticesZ[point] = model.verticesZ[point];
        }

        if (replaceAlpha) {
            triangleAlpha = model.triangleAlpha;
        } else {
            if (sharedTriangleAlpha.length < trianglesCount) {
                sharedTriangleAlpha = new byte[trianglesCount + 100];
            }
            triangleAlpha = sharedTriangleAlpha;
            if (model.triangleAlpha == null) {
                for (int face = 0; face < trianglesCount; face++) {
                    triangleAlpha[face] = 0;
                }
            } else {
                System.arraycopy(model.triangleAlpha, 0, triangleAlpha, 0, trianglesCount);
            }
        }

        drawType = model.drawType;
        colors = model.colors;
        renderPriorities = model.renderPriorities;
        facePriority = model.facePriority;
        faceGroups = model.faceGroups;
        vertexGroups = model.vertexGroups;
        trianglesX = model.trianglesX;
        trianglesY = model.trianglesY;
        trianglesZ = model.trianglesZ;
        colorsX = model.colorsX;
        colorsY = model.colorsY;
        colorsZ = model.colorsZ;
        texturesX = model.texturesX;
        texturesY = model.texturesY;
        texturesZ = model.texturesZ;
        textures = model.textures;
        textureTypes = model.textureTypes;
        materials = model.materials;

        //New
        vertexNormalsOffsets = model.vertexNormalsOffsets;
        vertexNormalsX = model.vertexNormalsX; //TODO uncomment
        vertexNormalsY = model.vertexNormalsY;
        vertexNormalsZ = model.vertexNormalsZ;
        faceTextureUVCoordinates = model.faceTextureUVCoordinates;

        resetBounds();
    }

    private int getFirstIdenticalVertexId(final Model model, final int vertex) {
        int vertexId = -1;
        final int x = model.verticesX[vertex];
        final int y = model.verticesY[vertex];
        final int z = model.verticesZ[vertex];
        for (int v = 0; v < this.verticesCount; v++) {
            if (x != this.verticesX[v] || y != this.verticesY[v] || z != this.verticesZ[v]) {
                continue;
            }
            vertexId = v;
            break;
        }

        if (vertexId == -1) {
            this.verticesX[this.verticesCount] = x;
            this.verticesY[this.verticesCount] = y;
            this.verticesZ[this.verticesCount] = z;
            if (model.vertexData != null) {
                this.vertexData[this.verticesCount] = model.vertexData[vertex];
            }
            vertexId = this.verticesCount++;

        }
        return vertexId;
    }

    public void calculateBoundsCylinder() {
        if (this.boundsType != 1) {
            this.boundsType = 1;
            super.modelBaseY = 0;
            diagonal2DAboveOrigin = 0;
            maxY = 0;
            for (int vertex = 0; vertex < verticesCount; vertex++) {
                final int x = verticesX[vertex];
                final int y = verticesY[vertex];
                final int z = verticesZ[vertex];
                if (-y > super.modelBaseY) {
                    super.modelBaseY = -y;
                }
                if (y > maxY) {
                    maxY = y;
                }
                final int bounds = x * x + z * z;
                if (bounds > diagonal2DAboveOrigin) {
                    diagonal2DAboveOrigin = bounds;
                }
            }

            diagonal2DAboveOrigin = (int)(Math.sqrt(diagonal2DAboveOrigin) + 0.98999999999999999);
            diagonal3DAboveOrigin = (int)(Math.sqrt(diagonal2DAboveOrigin * diagonal2DAboveOrigin + super.modelBaseY * super.modelBaseY) + 0.98999999999999999);
            diagonal3D = diagonal3DAboveOrigin + (int)(Math.sqrt(diagonal2DAboveOrigin * diagonal2DAboveOrigin + maxY * maxY) + 0.98999999999999999);
        }
    }

    void calculateDiagonals() {
        if (this.boundsType != 2) {
            this.boundsType = 2;
            this.diagonal2DAboveOrigin = 0;

            for (int count = 0; count < this.verticesCount; ++count) {
                int x = this.verticesX[count];
                int y = this.verticesY[count];
                int z = this.verticesZ[count];
                int bounds = x * x + z * z + y * y;
                if (bounds > this.diagonal2DAboveOrigin) {
                    this.diagonal2DAboveOrigin = bounds;
                }
            }

            this.diagonal2DAboveOrigin = (int)(Math.sqrt((double)this.diagonal2DAboveOrigin) + 0.99D);
            this.diagonal3DAboveOrigin = this.diagonal2DAboveOrigin;
            this.diagonal3D = this.diagonal2DAboveOrigin + this.diagonal2DAboveOrigin;
        }
    }

    public void normalise() {
        super.modelBaseY = 0;
        maxY = 0;
        for (int vertex = 0; vertex < verticesCount; vertex++) {
            final int y = verticesY[vertex];
            if (-y > super.modelBaseY) {
                super.modelBaseY = -y;
            }
            if (y > maxY) {
                maxY = y;
            }
        }

        this.diagonal3DAboveOrigin = (int)(Math.sqrt(diagonal2DAboveOrigin * diagonal2DAboveOrigin + super.modelBaseY * super.modelBaseY) + 0.98999999999999999);
        this.diagonal3D = diagonal3DAboveOrigin + (int)(Math.sqrt(diagonal2DAboveOrigin * diagonal2DAboveOrigin + maxY * maxY) + 0.98999999999999999);
    }

    public void calculateBounds() {
        if (!this.isBoundsCalculated) {
            super.modelBaseY = 0;
            diagonal2DAboveOrigin = 0;
            maxY = 0;
            minX = 0xf423f;
            maxX = 0xfff0bdc1;
            maxZ = 0xfffe7961;
            minZ = 0x1869f;
            for (int vertex = 0; vertex < verticesCount; vertex++) {
                final int x = verticesX[vertex];
                final int y = verticesY[vertex];
                final int z = verticesZ[vertex];
                if (x < minX) {
                    minX = x;
                }
                if (x > maxX) {
                    maxX = x;
                }
                if (z < minZ) {
                    minZ = z;
                }
                if (z > maxZ) {
                    maxZ = z;
                }
                if (-y > super.modelBaseY) {
                    super.modelBaseY = -y;
                }
                if (y > maxY) {
                    maxY = y;
                }
                final int bounds = x * x + z * z;
                if (bounds > diagonal2DAboveOrigin) {
                    diagonal2DAboveOrigin = bounds;
                }
            }
            this.isBoundsCalculated = true;
        }
    }

    public void generateBones() {
        if (vertexData != null) {
            int ai[] = new int[256];
            int j = 0;
            for (int l = 0; l < verticesCount; l++) {
                int j1 = vertexData[l];
                ai[j1]++;
                if (j1 > j)
                    j = j1;
            }
            vertexGroups = new int[j + 1][];
            for (int k1 = 0; k1 <= j; k1++) {
                vertexGroups[k1] = new int[ai[k1]];
                ai[k1] = 0;
            }
            for (int j2 = 0; j2 < verticesCount; j2++) {
                int l2 = vertexData[j2];
                vertexGroups[l2][ai[l2]++] = j2;
            }
            vertexData = null;
        }
        if (triangleData != null) {
            int ai1[] = new int[256];
            int k = 0;
            for (int i1 = 0; i1 < trianglesCount; i1++) {
                int l1 = triangleData[i1];
                ai1[l1]++;
                if (l1 > k)
                    k = l1;
            }
            faceGroups = new int[k + 1][];
            for (int uid = 0; uid <= k; uid++) {
                faceGroups[uid] = new int[ai1[uid]];
                ai1[uid] = 0;
            }
            for (int k2 = 0; k2 < trianglesCount; k2++) {
                int i3 = triangleData[k2];
                faceGroups[i3][ai1[i3]++] = k2;
            }
            triangleData = null;
        }
    }

    private void transform(int animationType, int skinArray[], int x, int y, int z) {

        int length = skinArray.length;
        if (animationType == 0) {
            int i = 0;
            transformTempX = 0;
            transformTempY = 0;
            transformTempZ = 0;
            for (int k2 = 0; k2 < length; k2++) {
                int skin = skinArray[k2];
                if (skin < vertexGroups.length) {
                    int[] group = vertexGroups[skin];
                    for (int i5 = 0; i5 < group.length; i5++) {
                        int offset = group[i5];
                        transformTempX += verticesX[offset];
                        transformTempY += verticesY[offset];
                        transformTempZ += verticesZ[offset];
                        i++;
                    }

                }
            }

            if (i > 0) {
                transformTempX = (int) (transformTempX / i + x);
                transformTempY = (int) (transformTempY / i + y);
                transformTempZ = (int) (transformTempZ / i + z);
                return;
            } else {
                transformTempX = (int) x;
                transformTempY = (int) y;
                transformTempZ = (int) z;
                return;
            }
        }
        if (animationType == 1) {
            for (int k1 = 0; k1 < length; k1++) {
                int skin = skinArray[k1];
                if (skin < vertexGroups.length) {
                    int[] group = vertexGroups[skin];
                    for (int i4 = 0; i4 < group.length; i4++) {
                        int offset = group[i4];
                        verticesX[offset] += x;
                        verticesY[offset] += y;
                        verticesZ[offset] += z;
                    }

                }
            }

            return;
        }
        if (animationType == 2) {
            for (int l1 = 0; l1 < length; l1++) {
                int skin = skinArray[l1];
                if (skin < vertexGroups.length) {
                    int[] group = vertexGroups[skin];
                    for (int j4 = 0; j4 < group.length; j4++) {
                        int offset = group[j4];
                        verticesX[offset] -= transformTempX;
                        verticesY[offset] -= transformTempY;
                        verticesZ[offset] -= transformTempZ;
                        int xOff = (x & 0xff) * 8;
                        int yOff = (y & 0xff) * 8;
                        int zOff = (z & 0xff) * 8;
                        if (zOff != 0) {
                            int sin = SINE[zOff];
                            int cos = COSINE[zOff];
                            int loc = verticesY[offset] * sin + verticesX[offset] * cos >> 16;
                            verticesY[offset] = verticesY[offset] * cos - verticesX[offset] * sin >> 16;
                            verticesX[offset] = loc;
                        }
                        if (xOff != 0) {
                            int sin = SINE[xOff];
                            int cos = COSINE[xOff];
                            int loc = verticesY[offset] * cos - verticesZ[offset] * sin >> 16;
                            verticesZ[offset] = verticesY[offset] * sin + verticesZ[offset] * cos >> 16;
                            verticesY[offset] = loc;
                        }
                        if (yOff != 0) {
                            int sin = SINE[yOff];
                            int cos = COSINE[yOff];
                            int loc = verticesZ[offset] * sin + verticesX[offset] * cos >> 16;
                            verticesZ[offset] = verticesZ[offset] * cos - verticesX[offset] * sin >> 16;
                            verticesX[offset] = loc;
                        }
                        verticesX[offset] += transformTempX;
                        verticesY[offset] += transformTempY;
                        verticesZ[offset] += transformTempZ;
                    }

                }
            }

            return;
        }
        if (animationType == 3) {
            for (int uid = 0; uid < length; uid++) {
                int skin = skinArray[uid];
                if (skin < vertexGroups.length) {
                    int[] group = vertexGroups[skin];
                    for (int k4 = 0; k4 < group.length; k4++) {
                        int offset = group[k4];
                        verticesX[offset] -= transformTempX;
                        verticesY[offset] -= transformTempY;
                        verticesZ[offset] -= transformTempZ;
                        verticesX[offset] = (int) ((verticesX[offset] * x) / 128);
                        verticesY[offset] = (int) ((verticesY[offset] * y) / 128);
                        verticesZ[offset] = (int) ((verticesZ[offset] * z) / 128);
                        verticesX[offset] += transformTempX;
                        verticesY[offset] += transformTempY;
                        verticesZ[offset] += transformTempZ;
                    }

                }
            }

            return;
        }
        if (animationType == 5 && faceGroups != null && triangleAlpha != null) {
            for (int j2 = 0; j2 < length; j2++) {
                int skin = skinArray[j2];
                if (skin < faceGroups.length) {
                    int[] group = faceGroups[skin];
                    for (int l4 = 0; l4 < group.length; l4++) {
                        int var13 = group[l4];
                        int var14 = (this.triangleAlpha[var13] & 255) + x * 8;
                        if (var14 < 0) {
                            var14 = 0;
                        } else if (var14 > 255) {
                            var14 = 255;
                        }

                        this.triangleAlpha[var13] = (byte)var14;
                    }

                }
            }
        }
    }

    public void animate(int frameId) {
        if (vertexGroups == null)
            return;

        if (frameId == -1)
            return;

        Frame frame = Frame.method531(frameId);
        if (frame == null)
            return;

        FrameBase base = frame.base;
        transformTempX = 0;
        transformTempY = 0;
        transformTempZ = 0;
        for (int k = 0; k < frame.transformationCount; k++) {
            int l = frame.transformationIndices[k];
            transform(base.transformationType[l],
                    base.skinList[l],
                    frame.transformX[k],
                    frame.transformY[k],
                    frame.transformZ[k]);
        }

        this.resetBounds();
        invalidate();
    }


    public void animate2(int label[], int idle, int current) {
        if (current == -1)
            return;

        if (label == null || idle == -1) {
            animate(current);
            return;
        }
        Frame anim = Frame.method531(current);
        if (anim == null)
            return;

        Frame skin = Frame.method531(idle);
        if (skin == null) {
            animate(current);
            return;
        }
        FrameBase list = anim.base;
        transformTempX = 0;
        transformTempY = 0;
        transformTempZ = 0;
        int id = 0;
        int table = label[id++];
        for (int index = 0; index < anim.transformationCount; index++) {
            int condition;
            for (condition = anim.transformationIndices[index]; condition > table; table = label[id++]) {
            }

            if (condition != table || list.transformationType[condition] == 0) {
                this.transform(list.transformationType[condition], list.skinList[condition], anim.transformX[index], anim.transformY[index], anim.transformZ[index]);
            }
        }

        transformTempX = 0;
        transformTempY = 0;
        transformTempZ = 0;
        id = 0;
        table = label[id++];
        for (int index = 0; index < skin.transformationCount; index++) {
            int condition;
            for (condition = skin.transformationIndices[index]; condition > table; table = label[id++]) {
            }

            if (condition == table || list.transformationType[condition] == 0) {
                this.transform(list.transformationType[condition], list.skinList[condition], skin.transformX[index], skin.transformY[index], skin.transformZ[index]);
            }
        }

        this.resetBounds();
        invalidate();
    }

    public void rotate90Degrees() {
        for (int vertex = 0; vertex < verticesCount; vertex++) {
            int x = verticesX[vertex];
            verticesX[vertex] = verticesZ[vertex];
            verticesZ[vertex] = -x;
        }

        this.resetBounds();
        invalidate();
    }

    public void rotateZ(int factor) {
        int sin = SINE[factor];
        int cos = COSINE[factor];
        for (int point = 0; point < verticesCount; point++) {
            int y = verticesY[point] * cos - verticesZ[point] * sin >> 16;
            verticesZ[point] = verticesY[point] * sin + verticesZ[point] * cos >> 16;
            verticesY[point] = y;
        }

        this.resetBounds();
        invalidate();
    }

    public void offsetBy(int x, int y, int z) {
        for (int point = 0; point < verticesCount; point++) {
            verticesX[point] += x;
            verticesY[point] += y;
            verticesZ[point] += z;
        }

        this.resetBounds();
        invalidate();
    }

    public void recolor(int found, int replace) {
        if(colors != null) {
            for (int face = 0; face < trianglesCount; face++) {
                if (colors[face] == (short) found) {
                    colors[face] = (short) replace;
                }
            }
        }
    }

    public void retexture(short found, short replace) {
        if(materials != null) {
            for (int face = 0; face < trianglesCount; face++) {
                if (materials[face] == found) {
                    materials[face] = replace;
                }
            }
        }
    }

    public void mirror() {
        for (int vertex = 0; vertex < verticesCount; vertex++)
            verticesZ[vertex] = -verticesZ[vertex];

        for (int face = 0; face < trianglesCount; face++) {
            int newTriangleZ = trianglesX[face];
            trianglesX[face] = trianglesZ[face];
            trianglesZ[face] = newTriangleZ;
        }
    }

    public void scale(int x, int z, int y) {
        for (int index = 0; index < verticesCount; index++) {
            verticesX[index] = (verticesX[index] * x) / 128;
            verticesY[index] = (verticesY[index] * y) / 128;
            verticesZ[index] = (verticesZ[index] * z) / 128;
        }

        this.resetBounds();
        invalidate();
    }

    public void calculateVertexNormals() {
        if (this.normals == null) {
            this.normals = new VertexNormal[this.verticesCount];

            int var1;
            for (var1 = 0; var1 < this.verticesCount; ++var1) {
                this.normals[var1] = new VertexNormal();
            }

            for (var1 = 0; var1 < this.trianglesCount; ++var1) {
                int var2 = this.trianglesX[var1];
                int var3 = this.trianglesY[var1];
                int var4 = this.trianglesZ[var1];
                int var5 = this.verticesX[var3] - this.verticesX[var2];
                int var6 = this.verticesY[var3] - this.verticesY[var2];
                int var7 = this.verticesZ[var3] - this.verticesZ[var2];
                int var8 = this.verticesX[var4] - this.verticesX[var2];
                int var9 = this.verticesY[var4] - this.verticesY[var2];
                int var10 = this.verticesZ[var4] - this.verticesZ[var2];
                int var11 = var6 * var10 - var9 * var7;
                int var12 = var7 * var8 - var10 * var5;

                int var13;
                for (var13 = var5 * var9 - var8 * var6; var11 > 8192 || var12 > 8192 || var13 > 8192 || var11 < -8192 || var12 < -8192 || var13 < -8192; var13 >>= 1) {
                    var11 >>= 1;
                    var12 >>= 1;
                }

                int var14 = (int)Math.sqrt((double)(var11 * var11 + var12 * var12 + var13 * var13));
                if (var14 <= 0) {
                    var14 = 1;
                }

                var11 = var11 * 256 / var14;
                var12 = var12 * 256 / var14;
                var13 = var13 * 256 / var14;
                int var15; //should be byte
                if (this.drawType == null) {
                    var15 = 0;
                } else {
                    var15 = this.drawType[var1];
                }

                if (var15 == 0) {
                    VertexNormal var16 = this.normals[var2];
                    var16.x += var11;
                    var16.y += var12;
                    var16.z += var13;
                    ++var16.magnitude;
                    var16 = this.normals[var3];
                    var16.x += var11;
                    var16.y += var12;
                    var16.z += var13;
                    ++var16.magnitude;
                    var16 = this.normals[var4];
                    var16.x += var11;
                    var16.y += var12;
                    var16.z += var13;
                    ++var16.magnitude;
                } else if (var15 == 1) {
                    if (this.faceNormals == null) {
                        this.faceNormals = new FaceNormal[this.trianglesCount];
                    }

                    FaceNormal var17 = this.faceNormals[var1] = new FaceNormal();
                    var17.x = var11;
                    var17.y = var12;
                    var17.z = var13;
                }
            }

        }
    }

    public void light(int ambient, int contrast, int x, int y, int z, boolean flatShading) {
        this.calculateVertexNormals();
        int magnitude = (int)Math.sqrt((double)(z * z + x * x + y * y));
        int var7 = magnitude * contrast >> 8;
        Model model = new Model();
        model.colorsX = new int[this.trianglesCount];
        model.colorsY = new int[this.trianglesCount];
        model.colorsZ = new int[this.trianglesCount];
        if (this.texturesCount > 0 && this.textures != null) {
            int[] var9 = new int[this.texturesCount];

            int var10;
            for (var10 = 0; var10 < this.trianglesCount; ++var10) {
                if (this.textures[var10] != -1) {
                    ++var9[this.textures[var10] & 255];
                }
            }

            model.texturesCount = 0;

            for (var10 = 0; var10 < this.texturesCount; ++var10) {
                if (var9[var10] > 0 && this.textureTypes[var10] == 0) {
                    ++model.texturesCount;
                }
            }

            model.texturesX = new short[model.texturesCount]; //should be int (Model is int, ModelData is short)
            model.texturesY = new short[model.texturesCount]; //should be int
            model.texturesZ = new short[model.texturesCount]; //should be int
            var10 = 0;

            int var11;
            for (var11 = 0; var11 < this.texturesCount; ++var11) {
                if (var9[var11] > 0 && this.textureTypes[var11] == 0) {
                    model.texturesX[var10] = (short) (this.texturesX[var11] & '\uffff'); //should be int (short cast redundant)
                    model.texturesY[var10] = (short) (this.texturesY[var11] & '\uffff'); //should be int (short cast redundant)
                    model.texturesZ[var10] = (short) (this.texturesZ[var11] & '\uffff'); //should be int (short cast redundant)
                    var9[var11] = var10++;
                } else {
                    var9[var11] = -1;
                }
            }

            model.textures = new byte[this.trianglesCount];

            for (var11 = 0; var11 < this.trianglesCount; ++var11) {
                if (this.textures[var11] != -1) {
                    model.textures[var11] = (byte)var9[this.textures[var11] & 255];
                } else {
                    model.textures[var11] = -1;
                }
            }
        }

        for (int var16 = 0; var16 < this.trianglesCount; ++var16) {
            int var17; //should be byte
            if (this.drawType == null) {
                var17 = 0;
            } else {
                var17 = this.drawType[var16];
            }

            byte var18;
            if (this.triangleAlpha == null) {
                var18 = 0;
            } else {
                var18 = this.triangleAlpha[var16];
            }

            short var12;
            if (this.materials == null) {
                var12 = -1;
            } else {
                var12 = this.materials[var16];
            }

            if (var18 == -2) {
                var17 = 3;
            }

            if (var18 == -1) {
                var17 = 2;
            }

            VertexNormal var13;
            int var14;
            FaceNormal var19;
            if (var12 == -1) {
                if (var17 != 0) {
                    if (var17 == 1) {
                        var19 = this.faceNormals[var16];
                        var14 = (y * var19.y + z * var19.z + x * var19.x) / (var7 / 2 + var7) + ambient;
                        model.colorsX[var16] = light(this.colors[var16] & '\uffff', var14);
                        model.colorsZ[var16] = -1;
                    } else if (var17 == 3) {
                        model.colorsX[var16] = 128;
                        model.colorsZ[var16] = -1;
                    } else {
                        model.colorsZ[var16] = -2;
                    }
                } else {
                    int var15 = this.colors[var16] & '\uffff';
                    if (this.vertexNormalsOffsets != null && this.vertexNormalsOffsets[this.trianglesX[var16]] != null) {
                        var13 = this.vertexNormalsOffsets[this.trianglesX[var16]];
                    } else {
                        var13 = this.normals[this.trianglesX[var16]];
                    }

                    var14 = (y * var13.y + z * var13.z + x * var13.x) / (var7 * var13.magnitude) + ambient;
                    model.colorsX[var16] = light(var15, var14);
                    if (this.vertexNormalsOffsets != null && this.vertexNormalsOffsets[this.trianglesY[var16]] != null) {
                        var13 = this.vertexNormalsOffsets[this.trianglesY[var16]];
                    } else {
                        var13 = this.normals[this.trianglesY[var16]];
                    }

                    var14 = (y * var13.y + z * var13.z + x * var13.x) / (var7 * var13.magnitude) + ambient;
                    model.colorsY[var16] = light(var15, var14);
                    if (this.vertexNormalsOffsets != null && this.vertexNormalsOffsets[this.trianglesZ[var16]] != null) {
                        var13 = this.vertexNormalsOffsets[this.trianglesZ[var16]];
                    } else {
                        var13 = this.normals[this.trianglesZ[var16]];
                    }

                    var14 = (y * var13.y + z * var13.z + x * var13.x) / (var7 * var13.magnitude) + ambient;
                    model.colorsZ[var16] = light(var15, var14);
                }
            } else if (var17 != 0) {
                if (var17 == 1) {
                    var19 = this.faceNormals[var16];
                    var14 = (y * var19.y + z * var19.z + x * var19.x) / (var7 / 2 + var7) + ambient;
                    model.colorsX[var16] = light(var14);
                    model.colorsZ[var16] = -1;
                } else {
                    model.colorsZ[var16] = -2;
                }
            } else {
                if (this.vertexNormalsOffsets != null && this.vertexNormalsOffsets[this.trianglesX[var16]] != null) {
                    var13 = this.vertexNormalsOffsets[this.trianglesX[var16]];
                } else {
                    var13 = this.normals[this.trianglesX[var16]];
                }

                var14 = (y * var13.y + z * var13.z + x * var13.x) / (var7 * var13.magnitude) + ambient;
                model.colorsX[var16] = light(var14);
                if (this.vertexNormalsOffsets != null && this.vertexNormalsOffsets[this.trianglesY[var16]] != null) {
                    var13 = this.vertexNormalsOffsets[this.trianglesY[var16]];
                } else {
                    var13 = this.normals[this.trianglesY[var16]];
                }

                var14 = (y * var13.y + z * var13.z + x * var13.x) / (var7 * var13.magnitude) + ambient;
                model.colorsY[var16] = light(var14);
                if (this.vertexNormalsOffsets != null && this.vertexNormalsOffsets[this.trianglesZ[var16]] != null) {
                    var13 = this.vertexNormalsOffsets[this.trianglesZ[var16]];
                } else {
                    var13 = this.normals[this.trianglesZ[var16]];
                }

                var14 = (y * var13.y + z * var13.z + x * var13.x) / (var7 * var13.magnitude) + ambient;
                model.colorsZ[var16] = light(var14);
            }
        }

        this.generateBones();
        model.verticesCount = this.verticesCount;
        model.verticesX = this.verticesX;
        model.verticesY = this.verticesY;
        model.verticesZ = this.verticesZ;
        model.trianglesCount = this.trianglesCount;
        model.trianglesX = this.trianglesX;
        model.trianglesY = this.trianglesY;
        model.trianglesZ = this.trianglesZ;
        model.renderPriorities = this.renderPriorities;
        model.triangleAlpha = this.triangleAlpha;
        model.facePriority = this.facePriority;
        model.vertexGroups = this.vertexGroups;
        model.faceGroups = this.faceGroups;
        model.materials = this.materials;
        model.animayaGroups = this.animayaGroups;
        model.animayaScales = this.animayaScales;
        this.colorsX = model.colorsX;
        this.colorsY = model.colorsY;
        this.colorsZ = model.colorsZ;
        this.texturesCount = model.texturesCount;
        this.textures = model.textures;
        this.texturesX = model.texturesX;
        this.texturesY = model.texturesY;
        this.texturesZ = model.texturesZ;
        this.texturesZ = model.texturesZ;

        if (flatShading) {
            calculateBoundsCylinder();
        } else {
            vertexNormalsOffsets = new VertexNormal[verticesCount];
            for (int point = 0; point < verticesCount; point++) {
                VertexNormal norm = super.normals[point];
                VertexNormal merge = vertexNormalsOffsets[point] = new VertexNormal();
                merge.x = norm.x;
                merge.y = norm.y;
                merge.z = norm.z;
                merge.magnitude = norm.magnitude;
            }

            calculateBounds();
        }

        resetBounds();

        if (textures == null) {
            vertexNormals();
        }

        //Mixins
        if (faceTextureUVCoordinates == null)
        {
            computeTextureUvCoordinates();
        }

        VertexNormal[] vertexNormals2 = normals;
        VertexNormal[] vertexVertices = vertexNormalsOffsets;

        if (vertexNormals2 != null && vertexNormalsX == null)
        {
            int verticesCount = getVerticesCount();

            vertexNormalsX = new int[verticesCount];
            vertexNormalsY = new int[verticesCount];
            vertexNormalsZ = new int[verticesCount];

            for (int i = 0; i < verticesCount; ++i) {
                VertexNormal vertexNormal;

                if (vertexVertices != null && (vertexNormal = vertexVertices[i]) != null) {
                    vertexNormalsX[i] = vertexNormal.getX();
                    vertexNormalsY[i] = vertexNormal.getY();
                    vertexNormalsZ[i] = vertexNormal.getZ();
                } else if ((vertexNormal = vertexNormals2[i]) != null) {
                    vertexNormalsX[i] = vertexNormal.getX();
                    vertexNormalsY[i] = vertexNormal.getY();
                    vertexNormalsZ[i] = vertexNormal.getZ();
                }
            }
        }

        this.normals = model.normals;
        this.vertexNormalsOffsets = model.vertexNormalsOffsets;

    }

    public final void setLighting(int intensity, int specular, int x, int y, int z) {
        setLighting(intensity, specular, x, y, z, false);
    }

    public final void setLighting(int intensity, int specular, int x, int y, int z, boolean player) {
        for (int face = 0; face < trianglesCount; face++) {
            int a = trianglesX[face];
            int b = trianglesY[face];
            int c = trianglesZ[face];
            if(materials != null) {
                if(player) {
                    //These checks are all important! - black triangle check
                    if(triangleAlpha != null && colors != null) {
                        if(colors[face] == 0 && renderPriorities[face] == 0) {
                            if(drawType[face] == 2 && materials[face] == -1) {
                                triangleAlpha[face] = (byte) 255;
                            }
                        }
                    } else if(triangleAlpha == null) {
                        if(colors[face] == 0 && renderPriorities[face] == 0) {
                            if(materials[face] == -1) {
                                triangleAlpha = new byte[trianglesCount];
                            }
                        }
                    }
                }
            }
            if (drawType == null) {
                int hsl = colors[face] & '\uffff';
                VertexNormal vertex = super.normals[a];
                int dir_light_intensity = intensity + (x * vertex.x + y * vertex.y + z * vertex.z) / (specular * vertex.magnitude);
                colorsX[face] = light(hsl, dir_light_intensity, 0);

                vertex = super.normals[b];
                dir_light_intensity = intensity + (x * vertex.x + y * vertex.y + z * vertex.z) / (specular * vertex.magnitude);
                colorsY[face] = light(hsl, dir_light_intensity, 0);

                vertex = super.normals[c];
                dir_light_intensity = intensity + (x * vertex.x + y * vertex.y + z * vertex.z) / (specular * vertex.magnitude);
                colorsZ[face] = light(hsl, dir_light_intensity, 0);
            } else if ((drawType[face] & 1) == 0) {
                int type = drawType[face];
                int hsl = colors[face] & '\uffff';
                VertexNormal vertex = super.normals[a];
                int dir_light_intensity = intensity + (x * vertex.x + y * vertex.y + z * vertex.z) / (specular * vertex.magnitude);
                colorsX[face] = light(hsl, dir_light_intensity, type);

                vertex = super.normals[b];
                dir_light_intensity = intensity + (x * vertex.x + y * vertex.y + z * vertex.z) / (specular * vertex.magnitude);
                colorsY[face] = light(hsl, dir_light_intensity, type);

                vertex = super.normals[c];
                dir_light_intensity = intensity + (x * vertex.x + y * vertex.y + z * vertex.z) / (specular * vertex.magnitude);
                colorsZ[face] = light(hsl, dir_light_intensity, type);
            }
        }
        super.normals = null;
        vertexNormalsOffsets = null;
        vertexData = null;
        triangleData = null;
        if (drawType != null) {
            for (int face = 0; face < trianglesCount; face++)
                if ((drawType[face] & 2) == 2)
                    return;
        }
        colors = null;
    }

    public static final int light(int light) {
        if(light >= 2) {
            if(light > 126) {
                light = 126;
            }
        } else {
            light = 2;
        }
        return light;
    }

    public static final int light(int hsl, int light) {
        light = light * (hsl & 127) >> 7;
        if(light < 2) {
            light = 2;
        } else if(light > 126) {
            light = 126;
        }
        return (hsl & '\uff80') + light;
    }

    public static final int light(int hsl, int light, int type) {
        if ((type & 2) == 2)
            return light(light);

        return light(hsl, light);
    }

    public void renderModel(final int rotationY, final int rotationZ, final int rotationXW, final int translationX, final int translationY, final int translationZ) {

        if (this.boundsType != 2 && this.boundsType != 1) {
            this.calculateDiagonals();
        }

        final int centerX = Rasterizer3D.originViewX;
        final int centerY = Rasterizer3D.originViewY;
        final int sineY = SINE[rotationY];
        final int cosineY = COSINE[rotationY];
        final int sineZ = SINE[rotationZ];
        final int cosineZ = COSINE[rotationZ];
        final int sineXW = SINE[rotationXW];
        final int cosineXW = COSINE[rotationXW];
        final int transformation = translationY * sineXW + translationZ * cosineXW >> 16;
        for (int vertex = 0; vertex < verticesCount; vertex++) {
            int x = this.verticesX[vertex];
            int y = this.verticesY[vertex];
            int z = this.verticesZ[vertex];
            if (rotationZ != 0) {
                final int newX = y * sineZ + x * cosineZ >> 16;
                y = y * cosineZ - x * sineZ >> 16;
                x = newX;
            }
            if (rotationY != 0) {
                final int newX = z * sineY + x * cosineY >> 16;
                z = z * cosineY - x * sineY >> 16;
                x = newX;
            }
            x += translationX;
            y += translationY;
            z += translationZ;
            final int newY = y * cosineXW - z * sineXW >> 16;
            z = y * sineXW + z * cosineXW >> 16;
            y = newY;
            vertexScreenZ[vertex] = z - transformation;
            vertexScreenX[vertex] = centerX + (x << 9) / z;
            vertexScreenY[vertex] = centerY + (y << 9) / z;
            if (texturesCount > 0) {
                vertexMovedX[vertex] = x;
                vertexMovedY[vertex] = y;
                vertexMovedZ[vertex] = z;
            }
        }

        try {
            this.withinObject(false, false, 0);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private static boolean mouseInViewport = false;

    public static void cursorCalculations() {
        int mouseX = MouseHandler.mouseX;
        int mouseY = MouseHandler.mouseY;
        if (MouseHandler.lastButton != 0) {
            mouseX = MouseHandler.saveClickX;
            mouseY = MouseHandler.saveClickY;
        }

        if (mouseX >= Client.instance.getViewportXOffset() && mouseX < Client.instance.getViewportXOffset() + Client.instance.getViewportWidth() && mouseY >= Client.instance.getViewportYOffset() && mouseY < Client.instance.getViewportHeight() + Client.instance.getViewportYOffset()) {
            cursorX = mouseX - Client.instance.getViewportXOffset();
            cursorY = mouseY - Client.instance.getViewportYOffset();
            mouseInViewport = true;
        } else {
            mouseInViewport = false;
        }
        objectsHovering = 0;
    }

    public void calculateBoundingBox(int orientation) {
        if (!this.aabb.containsKey(orientation)) {
            int minX = 0;
            int minZ = 0;
            int minY = 0;
            int maxX = 0;
            int maxZ = 0;
            int maxY = 0;
            int cosine = COSINE[orientation];
            int sine = SINE[orientation];

            for(int vert = 0; vert < this.verticesCount; ++vert) {
                int x = Rasterizer3D.method4046(this.verticesX[vert], this.verticesZ[vert], cosine, sine);
                int y = this.verticesY[vert];
                int z = Rasterizer3D.method4046(this.verticesX[vert], this.verticesZ[vert], cosine, sine);
                if (x < minX) {
                    minX = x;
                }

                if (x > maxX) {
                    maxX = x;
                }

                if (y < minZ) {
                    minZ = y;
                }

                if (y > maxZ) {
                    maxZ = y;
                }

                if (z < minY) {
                    minY = z;
                }

                if (z > maxY) {
                    maxY = z;
                }
            }

            AABB aabb = new AABB((maxX + minX) / 2, (maxZ + minZ) / 2, (maxY + minY) / 2, (maxX - minX + 1) / 2, (maxZ - minZ + 1) / 2, (maxY - minY + 1) / 2);

            if (aabb.xMidOffset < 32) {
                aabb.xMidOffset = 32;
            }

            if (aabb.zMidOffset < 32) {
                aabb.zMidOffset = 32;
            }

            if (this.singleTile) {
                aabb.xMidOffset += 8;
                aabb.zMidOffset += 8;
            }

            this.aabb.put(orientation, aabb);
        }

    }

    //Scene models
    @Override
    public final void renderAtPoint(int orientation, int pitchSine, int pitchCos, int yawSin, int yawCos, int offsetX, int offsetY, int offsetZ, long uid) {
        if (this.boundsType != 1) {
            this.calculateBoundsCylinder();
        }

        calculateBoundingBox(orientation);
        int sceneX = offsetZ * yawCos - offsetX * yawSin >> 16;
        int sceneY = offsetY * pitchSine + sceneX * pitchCos >> 16;
        int dimensionSinY = diagonal2DAboveOrigin * pitchCos >> 16;
        int pos = sceneY + dimensionSinY;
        final boolean gpu = Client.instance.isGpu() && Rasterizer3D.world;
        if (pos <= 50 || (sceneY >= 3500 && !gpu))
            return;
        int xRot = offsetZ * yawSin + offsetX * yawCos >> 16;
        int objX = (xRot - diagonal2DAboveOrigin) * Rasterizer3D.fieldOfView;
        if (objX / pos >= Rasterizer2D.viewportCenterX)
            return;

        int objWidth = (xRot + diagonal2DAboveOrigin) * Rasterizer3D.fieldOfView;
        if (objWidth / pos <= -Rasterizer2D.viewportCenterX)
            return;

        int yRot = offsetY * pitchCos - sceneX * pitchSine >> 16;
        int dimensionCosY = diagonal2DAboveOrigin * pitchSine >> 16;

        int var20 = (pitchCos * this.maxY >> 16) + dimensionCosY;
        int objHeight = (yRot + var20) * Rasterizer3D.fieldOfView;
        if (objHeight / pos <= -Rasterizer2D.viewportCenterY)
            return;

        int offset = dimensionCosY + (super.modelBaseY * pitchCos >> 16);
        int objY = (yRot - offset) * Rasterizer3D.fieldOfView;
        if (objY / pos >= Rasterizer2D.viewportCenterY)
            return;

        int size = dimensionSinY + (super.modelBaseY * pitchSine >> 16);

        boolean var25 = false;
        boolean nearSight = false;
        if (sceneY - size <= 50)
            nearSight = true;

        boolean inView = nearSight || this.texturesCount > 0;

        boolean highlighted = false;


        if (DEBUG_MODELS) {
            int x = ObjectKeyUtil.getObjectX(uid);
            int y = ObjectKeyUtil.getObjectY(uid);
            int opcode = ObjectKeyUtil.getObjectOpcode(uid);
            int id = ObjectKeyUtil.getObjectId(uid);

            System.out.println("Render at Point , ID: "+id+" at "+x+", "+y+" = "+uid);
        }

        if (uid > 0 && mouseInViewport) { // var32 should replace (uid > 0) in osrs, but does not work for older maps (cox pillars "null" have menus, agility obstacles/levers don't)
            if (DEBUG_MODELS) {
                int x = ObjectKeyUtil.getObjectX(uid);
                int y = ObjectKeyUtil.getObjectY(uid);
                int opcode = ObjectKeyUtil.getObjectOpcode(uid);
                int id = ObjectKeyUtil.getObjectId(uid);

                System.out.println("Render at Point Hover, ID: "+id+" at "+x+", "+y+" = "+uid);
            }

            boolean withinBounds = false;

            byte distanceMin = 50;
            short distanceMax = 3500;
            int var43 = (cursorX - Rasterizer3D.originViewX) * distanceMin / Rasterizer3D.fieldOfView;
            int var44 = (cursorY - Rasterizer3D.originViewY) * distanceMin / Rasterizer3D.fieldOfView;
            int var45 = (cursorX - Rasterizer3D.originViewX) * distanceMax / Rasterizer3D.fieldOfView;
            int var46 = (cursorY - Rasterizer3D.originViewY) * distanceMax / Rasterizer3D.fieldOfView;
            int var47 = Rasterizer3D.method4045(var44, distanceMin, SceneGraph.camUpDownX, SceneGraph.camUpDownY);
            int var53 = Rasterizer3D.method4046(var44, distanceMin, SceneGraph.camUpDownX, SceneGraph.camUpDownY);
            var44 = var47;
            var47 = Rasterizer3D.method4045(var46, distanceMax, SceneGraph.camUpDownX, SceneGraph.camUpDownY);
            int var54 = Rasterizer3D.method4046(var46, distanceMax, SceneGraph.camUpDownX, SceneGraph.camUpDownY);
            var46 = var47;
            var47 = Rasterizer3D.method4025(var43, var53, SceneGraph.camLeftRightX, SceneGraph.camLeftRightY);
            var53 = Rasterizer3D.method4044(var43, var53, SceneGraph.camLeftRightX, SceneGraph.camLeftRightY);
            var43 = var47;
            var47 = Rasterizer3D.method4025(var45, var54, SceneGraph.camLeftRightX, SceneGraph.camLeftRightY);
            var54 = Rasterizer3D.method4044(var45, var54, SceneGraph.camLeftRightX, SceneGraph.camLeftRightY);
            int ViewportMouse_field2588 = (var43 + var47) / 2;
            int GZipDecompressor_field4821 = (var46 + var44) / 2;
            int class340_field4138 = (var54 + var53) / 2;
            int ViewportMouse_field2589 = (var47 - var43) / 2;
            int ItemComposition_field2148 = (var46 - var44) / 2;
            int User_field4308 = (var54 - var53) / 2;
            int class421_field4607 = Math.abs(ViewportMouse_field2589);
            int ViewportMouse_field2590 = Math.abs(ItemComposition_field2148);
            int class136_field1612 = Math.abs(User_field4308);

            AABB var50 = (AABB)this.aabb.get(orientation);
            int var37 = offsetX + var50.xMid;
            int var38 = offsetY + var50.yMid;
            int var39 = offsetZ + var50.zMid;
            var43 = ViewportMouse_field2588 - var37;
            var44 = GZipDecompressor_field4821 - var38;
            var45 = class340_field4138 - var39;
            if (Math.abs(var43) > var50.xMidOffset + class421_field4607) {
                withinBounds = false;
            } else if (Math.abs(var44) > var50.yMidOffset + ViewportMouse_field2590) {
                withinBounds = false;
            } else if (Math.abs(var45) > var50.zMidOffset + class136_field1612) {
                withinBounds = false;
            } else if (Math.abs(var45 * ItemComposition_field2148 - var44 * User_field4308) > var50.yMidOffset * class136_field1612 + var50.zMidOffset * ViewportMouse_field2590) {
                withinBounds = false;
            } else if (Math.abs(var43 * User_field4308 - var45 * ViewportMouse_field2589) > var50.zMidOffset * class421_field4607 + var50.xMidOffset * class136_field1612) {
                withinBounds = false;
            } else if (Math.abs(var44 * ViewportMouse_field2589 - var43 * ItemComposition_field2148) > var50.xMidOffset * ViewportMouse_field2590 + var50.yMidOffset * class421_field4607) {
                withinBounds = false;
            } else {
                withinBounds = true;
            }

            if (withinBounds) {
                if (this.singleTile) {
                    hoveringObjects[objectsHovering++] = uid;
                } else {
                    highlighted = true;
                }
            }
        }

        int sineX = 0;
        int cosineX = 0;
        if (orientation != 0) {
            sineX = SINE[orientation];
            cosineX = COSINE[orientation];
        }

        for (int index = 0; index < this.verticesCount; ++index) {
            int positionX = this.verticesX[index];
            int rasterY = this.verticesY[index];
            int positionZ = this.verticesZ[index];
            if (orientation != 0) {
                int rotatedX = positionZ * sineX + positionX * cosineX >> 16;
                positionZ = positionZ * cosineX - positionX * sineX >> 16;
                positionX = rotatedX;
            }

            positionX += offsetX;
            rasterY += offsetY;
            positionZ += offsetZ;

            int positionY = positionZ * yawSin + yawCos * positionX >> 16;
            positionZ = yawCos * positionZ - positionX * yawSin >> 16;
            positionX = positionY;
            positionY = pitchCos * rasterY - positionZ * pitchSine >> 16;
            positionZ = rasterY * pitchSine + pitchCos * positionZ >> 16;
            vertexScreenZ[index] = positionZ - sceneY;
            if (positionZ >= 50) {
                vertexScreenX[index] = positionX * Rasterizer3D.fieldOfView / positionZ + Rasterizer3D.originViewX;
                vertexScreenY[index] = positionY * Rasterizer3D.fieldOfView / positionZ + Rasterizer3D.originViewY;
            } else {
                vertexScreenX[index] = -5000;
                var25 = true;
            }

            if (inView) {
                vertexMovedX[index] = positionX;
                vertexMovedY[index] = positionY;
                vertexMovedZ[index] = positionZ;
            }
        }

        try {
            if (!gpu || (highlighted && !(Math.sqrt(offsetX * offsetX + offsetZ * offsetZ) > 35 * Perspective.LOCAL_TILE_SIZE))) {
                withinObject(var25, highlighted, uid);
            }
            if (gpu) {
                Client.instance.getDrawCallbacks().draw(this, orientation, pitchSine, pitchCos, yawSin, yawCos, offsetX, offsetY, offsetZ, uid);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean inBounds(int x, int y, int z, int screenX, int screenY, int screenZ, int size) {
        int height = cursorY + size;
        if (height < x && height < y && height < z) {
            return false;
        } else {
            height = cursorY - size;
            if (height > x && height > y && height > z) {
                return false;
            } else {
                height = cursorX + size;
                if (height < screenX && height < screenY && height < screenZ) {
                    return false;
                } else {
                    height = cursorX - size;
                    return height <= screenX || height <= screenY || height <= screenZ;
                }
            }
        }
    }

    final void withinObject(boolean var25, boolean highlighted, long uid) {
        final boolean gpu = Client.instance.isGpu() && Rasterizer3D.world;

        if (diagonal3D < 1600) {
            for (int diagonalIndex = 0; diagonalIndex < diagonal3D; diagonalIndex++) {
                depth[diagonalIndex] = 0;
            }

            int size = singleTile ? 20 : 5;

            int var15;
            int var16;
            int var18;
            for (int currentTriangle = 0; currentTriangle < this.trianglesCount; ++currentTriangle) {
                if (this.colorsZ[currentTriangle] != -2) {
                    int triX = this.trianglesX[currentTriangle];
                    int triY = this.trianglesY[currentTriangle];
                    int triZ = this.trianglesZ[currentTriangle];
                    int screenXX = vertexScreenX[triX];
                    int screenXY = vertexScreenX[triY];
                    int screenXZ = vertexScreenX[triZ];
                    int index;

                    if (gpu) {
                        if (screenXX == -5000 || screenXY == -5000 || screenXZ == -5000) {
                            continue;
                        }
                        if (highlighted && inBounds(vertexScreenY[triX], vertexScreenY[triY], vertexScreenY[triZ], screenXX, screenXY, screenXZ,size)) {
                            hoveringObjects[objectsHovering++] = uid;
                        }
                        continue;
                    }

                    if (!var25 || screenXX != -5000 && screenXY != -5000 && screenXZ != -5000) {
                        if (highlighted && inBounds(vertexScreenY[triX], vertexScreenY[triY], vertexScreenY[triZ], screenXX, screenXY, screenXZ, size)) {
                            hoveringObjects[objectsHovering++] = uid;
                            highlighted = false;
                        }

                        if ((screenXX - screenXY) * (vertexScreenY[triZ] - vertexScreenY[triY]) - (screenXZ - screenXY) * (vertexScreenY[triX] - vertexScreenY[triY]) > 0) {
                            outOfReach[currentTriangle] = false;
                            if (screenXX >= 0 && screenXY >= 0 && screenXZ >= 0 && screenXX <= Rasterizer3D.lastX && screenXY <= Rasterizer3D.lastX && screenXZ <= Rasterizer3D.lastX) {
                                hasAnEdgeToRestrict[currentTriangle] = false;
                            } else {
                                hasAnEdgeToRestrict[currentTriangle] = true;
                            }

                            index = (vertexScreenZ[triX] + vertexScreenZ[triY] + vertexScreenZ[triZ]) / 3 + this.diagonal3DAboveOrigin;
                            if (index < 0)
                                index = 0;

                            faceLists[index][depth[index]++] = currentTriangle;
                        }
                    } else {
                        index = vertexMovedX[triX];
                        var15 = vertexMovedX[triY];
                        var16 = vertexMovedX[triZ];
                        int var30 = vertexMovedY[triX];
                        var18 = vertexMovedY[triY];
                        int var19 = vertexMovedY[triZ];
                        int var20 = vertexMovedZ[triX];
                        int var21 = vertexMovedZ[triY];
                        int var22 = vertexMovedZ[triZ];
                        index -= var15;
                        var16 -= var15;
                        var30 -= var18;
                        var19 -= var18;
                        var20 -= var21;
                        var22 -= var21;
                        int var23 = var30 * var22 - var20 * var19;
                        int var24 = var20 * var16 - index * var22;
                        int var25a = index * var19 - var30 * var16;
                        if (var15 * var23 + var18 * var24 + var21 * var25a > 0) {
                            outOfReach[currentTriangle] = true;
                            int var26 = (vertexScreenZ[triX] + vertexScreenZ[triY] + vertexScreenZ[triZ]) / 3 + this.diagonal3DAboveOrigin;
                            faceLists[var26][depth[var26]++] = currentTriangle;
                        }
                    }
                }
            }
            if (gpu) {
                return;
            }
            if (this.renderPriorities == null) {
                for (int faceIndex = this.diagonal3D - 1; faceIndex >= 0; --faceIndex) {
                    int depth = Model.depth[faceIndex];
                    if (depth > 0) {
                        for (int index = 0; index < depth; ++index) {
                            this.drawFace(faceLists[faceIndex][index]);
                        }
                    }
                }

            } else {
                for (int currentIndex = 0; currentIndex < 12; ++currentIndex) {
                    anIntArray1673[currentIndex] = 0;
                    anIntArray1677[currentIndex] = 0;
                }

                for (int depthIndex = this.diagonal3D - 1; depthIndex >= 0; --depthIndex) {
                    int var8 = depth[depthIndex];
                    if (var8 > 0) {

                        for (int var10 = 0; var10 < var8; ++var10) {
                            int var11 = faceLists[depthIndex][var10];
                            byte var31 = this.renderPriorities[var11];
                            int var28 = anIntArray1673[var31]++;
                            anIntArrayArray1674[var31][var28] = var11;
                            if (var31 < 10) {
                                anIntArray1677[var31] += depthIndex;
                            } else if (var31 == 10) {
                                anIntArray1675[var28] = depthIndex;
                            } else {
                                anIntArray1676[var28] = depthIndex;
                            }
                        }
                    }
                }

                int var7 = 0;
                if (anIntArray1673[1] > 0 || anIntArray1673[2] > 0) {
                    var7 = (anIntArray1677[1] + anIntArray1677[2]) / (anIntArray1673[1] + anIntArray1673[2]);
                }

                int var8 = 0;
                if (anIntArray1673[3] > 0 || anIntArray1673[4] > 0) {
                    var8 = (anIntArray1677[3] + anIntArray1677[4]) / (anIntArray1673[3] + anIntArray1673[4]);
                }

                int var9 = 0;
                if (anIntArray1673[6] > 0 || anIntArray1673[8] > 0) {
                    var9 = (anIntArray1677[8] + anIntArray1677[6]) / (anIntArray1673[8] + anIntArray1673[6]);
                }

                int var11 = 0;
                int var12 = anIntArray1673[10];
                int[] var13 = anIntArrayArray1674[10];
                int[] var14 = anIntArray1675;
                if (var11 == var12) {
                    var11 = 0;
                    var12 = anIntArray1673[11];
                    var13 = anIntArrayArray1674[11];
                    var14 = anIntArray1676;
                }

                int var10;
                if (var11 < var12) {
                    var10 = var14[var11];
                } else {
                    var10 = -1000;
                }

                for (var15 = 0; var15 < 10; ++var15) {
                    while (var15 == 0 && var10 > var7) {
                        this.drawFace(var13[var11++]);
                        if (var11 == var12 && var13 != anIntArrayArray1674[11]) {
                            var11 = 0;
                            var12 = anIntArray1673[11];
                            var13 = anIntArrayArray1674[11];
                            var14 = anIntArray1676;
                        }

                        if (var11 < var12) {
                            var10 = var14[var11];
                        } else {
                            var10 = -1000;
                        }
                    }

                    while (var15 == 3 && var10 > var8) {
                        this.drawFace(var13[var11++]);
                        if (var11 == var12 && var13 != anIntArrayArray1674[11]) {
                            var11 = 0;
                            var12 = anIntArray1673[11];
                            var13 = anIntArrayArray1674[11];
                            var14 = anIntArray1676;
                        }

                        if (var11 < var12) {
                            var10 = var14[var11];
                        } else {
                            var10 = -1000;
                        }
                    }

                    while (var15 == 5 && var10 > var9) {
                        this.drawFace(var13[var11++]);
                        if (var11 == var12 && var13 != anIntArrayArray1674[11]) {
                            var11 = 0;
                            var12 = anIntArray1673[11];
                            var13 = anIntArrayArray1674[11];
                            var14 = anIntArray1676;
                        }

                        if (var11 < var12) {
                            var10 = var14[var11];
                        } else {
                            var10 = -1000;
                        }
                    }

                    var16 = anIntArray1673[var15];
                    int[] var17 = anIntArrayArray1674[var15];

                    for (var18 = 0; var18 < var16; ++var18) {
                        this.drawFace(var17[var18]);
                    }
                }

                while (var10 != -1000) {
                    this.drawFace(var13[var11++]);
                    if (var11 == var12 && var13 != anIntArrayArray1674[11]) {
                        var11 = 0;
                        var13 = anIntArrayArray1674[11];
                        var12 = anIntArray1673[11];
                        var14 = anIntArray1676;
                    }

                    if (var11 < var12) {
                        var10 = var14[var11];
                    } else {
                        var10 = -1000;
                    }
                }

            }
        }
    }

    public void drawFace(int face) { //method484
        DrawCallbacks callbacks = Client.instance.getDrawCallbacks();
        if (callbacks == null || !callbacks.drawFace(this, face))
        {
            if (outOfReach[face]) {
                faceRotation(face);
                return;
            }
            int triX = trianglesX[face];
            int triY = trianglesY[face];
            int triZ = trianglesZ[face];
            Rasterizer3D.textureOutOfDrawingBounds = hasAnEdgeToRestrict[face];
            if (triangleAlpha == null)
                Rasterizer3D.alpha = 0;
            else
                Rasterizer3D.alpha = triangleAlpha[face] & 0xff;

            int type;
            if (drawType == null)
                type = 0;
            else
                type = drawType[face] & 3;

            if (materials != null && materials[face] != -1) {
                int textureA = triX;
                int textureB = triY;
                int textureC = triZ;
                if (textures != null && textures[face] != -1) {
                    int coordinate = textures[face] & 0xff;
                    textureA = texturesX[coordinate];
                    textureB = texturesY[coordinate];
                    textureC = texturesZ[coordinate];
                }

                if (colorsZ[face] == -1 || type == 3) {
                    Rasterizer3D.drawTexturedTriangle(
                            vertexScreenY[triX], vertexScreenY[triY], vertexScreenY[triZ],
                            vertexScreenX[triX], vertexScreenX[triY], vertexScreenX[triZ],
                            colorsX[face], colorsX[face], colorsX[face],
                            vertexMovedX[textureA], vertexMovedX[textureB], vertexMovedX[textureC],
                            vertexMovedY[textureA], vertexMovedY[textureB], vertexMovedY[textureC],
                            vertexMovedZ[textureA], vertexMovedZ[textureB], vertexMovedZ[textureC],
                            materials[face]);
                } else {
                    Rasterizer3D.drawTexturedTriangle(
                            vertexScreenY[triX], vertexScreenY[triY], vertexScreenY[triZ],
                            vertexScreenX[triX], vertexScreenX[triY], vertexScreenX[triZ],
                            colorsX[face], colorsY[face], colorsZ[face],
                            vertexMovedX[textureA], vertexMovedX[textureB], vertexMovedX[textureC],
                            vertexMovedY[textureA], vertexMovedY[textureB], vertexMovedY[textureC],
                            vertexMovedZ[textureA], vertexMovedZ[textureB], vertexMovedZ[textureC],
                            materials[face]);
                }

            } else if(colorsZ[face] == -1) {//From old to new clause
                Rasterizer3D.drawFlatTriangle(
                        vertexScreenY[triX], vertexScreenY[triY], vertexScreenY[triZ],
                        vertexScreenX[triX], vertexScreenX[triY], vertexScreenX[triZ],
                        modelColors[colorsX[face]]);

            } else {
                if (type == 0) {
                    Rasterizer3D.drawShadedTriangle(vertexScreenY[triX], vertexScreenY[triY],
                            vertexScreenY[triZ], vertexScreenX[triX], vertexScreenX[triY],
                            vertexScreenX[triZ], colorsX[face], colorsY[face], colorsZ[face]);
                }
                if (type == 1) {
                    Rasterizer3D.drawFlatTriangle(vertexScreenY[triX], vertexScreenY[triY],
                            vertexScreenY[triZ], vertexScreenX[triX], vertexScreenX[triY],
                            vertexScreenX[triZ], modelColors[colorsX[face]]);
                }
            }

        }
    }

    private final void faceRotation(int triangle) {
        int centreX = Rasterizer3D.originViewX;
        int centreY = Rasterizer3D.originViewY;
        int counter = 0;
        int x = trianglesX[triangle];
        int y = trianglesY[triangle];
        int z = trianglesZ[triangle];
        int movedX = vertexMovedZ[x];
        int movedY = vertexMovedZ[y];
        int movedZ = vertexMovedZ[z];
        if (movedX >= 50) {
            xPosition[counter] = vertexScreenX[x];
            yPosition[counter] = vertexScreenY[x];
            zPosition[counter++] = colorsX[triangle];
        } else {
            int movedX2 = vertexMovedX[x];
            int movedY2 = vertexMovedY[x];
            int colour = colorsX[triangle];
            if (movedZ >= 50) {
                int k5 = (50 - movedX) * modelLocations[movedZ - movedX];
                xPosition[counter] = centreX + (movedX2 + ((vertexMovedX[z] - movedX2) * k5 >> 16)) * Rasterizer3D.fieldOfView / 50;
                yPosition[counter] = centreY + (movedY2 + ((vertexMovedY[z] - movedY2) * k5 >> 16)) * Rasterizer3D.fieldOfView / 50;
                zPosition[counter++] = colour + ((colorsZ[triangle] - colour) * k5 >> 16);
            }
            if (movedY >= 50) {
                int l5 = (50 - movedX) * modelLocations[movedY - movedX];
                xPosition[counter] = centreX + (movedX2 + ((vertexMovedX[y] - movedX2) * l5 >> 16)) * Rasterizer3D.fieldOfView / 50;
                yPosition[counter] = centreY + (movedY2 + ((vertexMovedY[y] - movedY2) * l5 >> 16)) * Rasterizer3D.fieldOfView / 50;
                zPosition[counter++] = colour + ((colorsY[triangle] - colour) * l5 >> 16);
            }
        }
        if (movedY >= 50) {
            xPosition[counter] = vertexScreenX[y];
            yPosition[counter] = vertexScreenY[y];
            zPosition[counter++] = colorsY[triangle];
        } else {
            int movedX2 = vertexMovedX[y];
            int movedY2 = vertexMovedY[y];
            int colour = colorsY[triangle];
            if (movedX >= 50) {
                int i6 = (50 - movedY) * modelLocations[movedX - movedY];
                xPosition[counter] = centreX + (movedX2 + ((vertexMovedX[x] - movedX2) * i6 >> 16)) * Rasterizer3D.fieldOfView / 50;
                yPosition[counter] = centreY + (movedY2 + ((vertexMovedY[x] - movedY2) * i6 >> 16)) * Rasterizer3D.fieldOfView / 50;
                zPosition[counter++] = colour + ((colorsX[triangle] - colour) * i6 >> 16);
            }
            if (movedZ >= 50) {
                int j6 = (50 - movedY) * modelLocations[movedZ - movedY];
                xPosition[counter] = centreX + (movedX2 + ((vertexMovedX[z] - movedX2) * j6 >> 16)) * Rasterizer3D.fieldOfView / 50;
                yPosition[counter] = centreY + (movedY2 + ((vertexMovedY[z] - movedY2) * j6 >> 16)) * Rasterizer3D.fieldOfView / 50;
                zPosition[counter++] = colour + ((colorsZ[triangle] - colour) * j6 >> 16);
            }
        }
        if (movedZ >= 50) {
            xPosition[counter] = vertexScreenX[z];
            yPosition[counter] = vertexScreenY[z];
            zPosition[counter++] = colorsZ[triangle];
        } else {
            int movedX2 = vertexMovedX[z];
            int movedY2 = vertexMovedY[z];
            int colour = colorsZ[triangle];
            if (movedY >= 50) {
                int k6 = (50 - movedZ) * modelLocations[movedY - movedZ];
                xPosition[counter] = centreX + (movedX2 + ((vertexMovedX[y] - movedX2) * k6 >> 16)) * Rasterizer3D.fieldOfView / 50;
                yPosition[counter] = centreY + (movedY2 + ((vertexMovedY[y] - movedY2) * k6 >> 16)) * Rasterizer3D.fieldOfView / 50;
                zPosition[counter++] = colour + ((colorsY[triangle] - colour) * k6 >> 16);
            }
            if (movedX >= 50) {
                int l6 = (50 - movedZ) * modelLocations[movedX - movedZ];
                xPosition[counter] = centreX + (movedX2 + ((vertexMovedX[x] - movedX2) * l6 >> 16)) * Rasterizer3D.fieldOfView / 50;
                yPosition[counter] = centreY + (movedY2 + ((vertexMovedY[x] - movedY2) * l6 >> 16)) * Rasterizer3D.fieldOfView / 50;
                zPosition[counter++] = colour + ((colorsX[triangle] - colour) * l6 >> 16);
            }
        }
        int xA = xPosition[0];
        int xB = xPosition[1];
        int xC = xPosition[2];
        int yA = yPosition[0];
        int yB = yPosition[1];
        int yC = yPosition[2];
        if ((xA - xB) * (yC - yB) - (yA - yB) * (xC - xB) > 0) {
            Rasterizer3D.textureOutOfDrawingBounds = false;
            int textureA = x;
            int textureB = y;
            int textureC = z;
            if (counter == 3) {
                if (xA < 0 || xB < 0 || xC < 0 || xA > Rasterizer2D.lastX || xB > Rasterizer2D.lastX || xC > Rasterizer2D.lastX) {
                    Rasterizer3D.textureOutOfDrawingBounds = true;
                }

                int drawType;
                if (this.drawType == null) {
                    drawType = 0;
                } else {
                    drawType = this.drawType[triangle] & 3;
                }

                if (materials != null && materials[triangle] != -1) {

                    if (textures != null && textures[triangle] != -1) {
                        int coordinate = textures[triangle] & 0xff;
                        textureA = texturesX[coordinate];
                        textureB = texturesY[coordinate];
                        textureC = texturesZ[coordinate];
                    }

                    if (colorsZ[triangle] == -1) {
                        Rasterizer3D.drawTexturedTriangle(
                                yA, yB, yC,
                                xA, xB, xC,
                                colorsX[triangle], colorsX[triangle], colorsX[triangle],
                                vertexMovedX[textureA], vertexMovedX[textureB], vertexMovedX[textureC],
                                vertexMovedY[textureA], vertexMovedY[textureB], vertexMovedY[textureC],
                                vertexMovedZ[textureA], vertexMovedZ[textureB], vertexMovedZ[textureC],
                                materials[triangle]);
                    } else {
                        Rasterizer3D.drawTexturedTriangle(
                                yA, yB, yC,
                                xA, xB, xC,
                                zPosition[0], zPosition[1], zPosition[2],
                                vertexMovedX[textureA], vertexMovedX[textureB], vertexMovedX[textureC],
                                vertexMovedY[textureA], vertexMovedY[textureB], vertexMovedY[textureC],
                                vertexMovedZ[textureA], vertexMovedZ[textureB], vertexMovedZ[textureC],
                                materials[triangle]);
                    }
                } else {
                    if (drawType == 0) {
                        Rasterizer3D.drawShadedTriangle(yA, yB, yC, xA, xB, xC, zPosition[0], zPosition[1], zPosition[2]);
                    } else if (drawType == 1) {
                        Rasterizer3D.drawFlatTriangle(yA, yB, yC, xA, xB, xC, modelColors[colorsX[triangle]]);
                    }
                }
            }
            if (counter == 4) {
                if (xA < 0 || xB < 0 || xC < 0 || xA > Rasterizer2D.lastX || xB > Rasterizer2D.lastX || xC > Rasterizer2D.lastX || xPosition[3] < 0 || xPosition[3] > Rasterizer2D.lastX) {
                    Rasterizer3D.textureOutOfDrawingBounds = true;
                }
                int drawType;
                if (this.drawType == null) {
                    drawType = 0;
                } else {
                    drawType = this.drawType[triangle] & 3;
                }

                if (materials != null && materials[triangle] != -1) {
                    if (textures != null && textures[triangle] != -1) {
                        int coordinate = textures[triangle] & 0xff;
                        textureA = texturesX[coordinate];
                        textureB = texturesY[coordinate];
                        textureC = texturesZ[coordinate];
                    }
                    if (colorsZ[triangle] == -1) {
                        Rasterizer3D.drawTexturedTriangle(
                                yA, yB, yC,
                                xA, xB, xC,
                                colorsX[triangle], colorsX[triangle], colorsX[triangle],
                                vertexMovedX[textureA], vertexMovedX[textureB], vertexMovedX[textureC],
                                vertexMovedY[textureA], vertexMovedY[textureB], vertexMovedY[textureC],
                                vertexMovedZ[textureA], vertexMovedZ[textureB], vertexMovedZ[textureC],
                                materials[triangle]);
                        Rasterizer3D.drawTexturedTriangle(
                                yA, yC, yPosition[3],
                                xA, xC, xPosition[3],
                                colorsX[triangle], colorsX[triangle], colorsX[triangle],
                                vertexMovedX[textureA], vertexMovedX[textureB], vertexMovedX[textureC],
                                vertexMovedY[textureA], vertexMovedY[textureB], vertexMovedY[textureC],
                                vertexMovedZ[textureA], vertexMovedZ[textureB], vertexMovedZ[textureC],
                                materials[triangle]);
                    } else {
                        Rasterizer3D.drawTexturedTriangle(
                                yA, yB, yC,
                                xA, xB, xC,
                                zPosition[0], zPosition[1], zPosition[2],
                                vertexMovedX[textureA], vertexMovedX[textureB], vertexMovedX[textureC],
                                vertexMovedY[textureA], vertexMovedY[textureB], vertexMovedY[textureC],
                                vertexMovedZ[textureA], vertexMovedZ[textureB], vertexMovedZ[textureC],
                                materials[triangle]);
                        Rasterizer3D.drawTexturedTriangle(
                                yA, yC, yPosition[3],
                                xA, xC, xPosition[3],
                                zPosition[0], zPosition[2], zPosition[3],
                                vertexMovedX[textureA], vertexMovedX[textureB], vertexMovedX[textureC],
                                vertexMovedY[textureA], vertexMovedY[textureB], vertexMovedY[textureC],
                                vertexMovedZ[textureA], vertexMovedZ[textureB], vertexMovedZ[textureC],
                                materials[triangle]);
                    }
                } else {
                    if (drawType == 0) {
                        Rasterizer3D.drawShadedTriangle(yA, yB, yC, xA, xB, xC, zPosition[0], zPosition[1], zPosition[2]);
                        Rasterizer3D.drawShadedTriangle(yA, yC, yPosition[3], xA, xC, xPosition[3], zPosition[0], zPosition[2], zPosition[3]);
                        return;
                    }
                    if (drawType == 1) {
                        int l8 = modelColors[colorsX[triangle]];
                        Rasterizer3D.drawFlatTriangle(yA, yB, yC, xA, xB, xC, l8);
                        Rasterizer3D.drawFlatTriangle(yA, yC, yPosition[3], xA, xC, xPosition[3], l8);
                    }
                }
            }
        }
    }

    private int boundsType;
    boolean isBoundsCalculated;

    public int animayaGroups[][];
    public int animayaScales[][];

    private float[] faceTextureUVCoordinates;
    private int[] vertexNormalsX, vertexNormalsY, vertexNormalsZ;

    public short[] materials;
    public byte[] textures;
    public byte[] textureTypes;

    public static int anInt1620;
    public static Model emptyModel = new Model();
    private static int sharedVerticesX[] = new int[2000];
    private static int sharedVerticesY[] = new int[2000];
    private static int sharedVerticesZ[] = new int[2000];
    private static byte sharedTriangleAlpha[] = new byte[2000];
    public int verticesCount;
    public int verticesX[];
    public int verticesY[];
    public int verticesZ[];
    public int trianglesCount;
    public int trianglesX[];
    public int trianglesY[];
    public int trianglesZ[];
    public int colorsX[];
    public int colorsY[];
    public int colorsZ[];
    public int drawType[];
    public byte[] renderPriorities;
    public byte triangleAlpha[];
    public short colors[];
    public byte facePriority = 0;
    public int texturesCount;
    public short texturesX[];
    public short texturesY[];
    public short texturesZ[];
    public int minX;
    public int maxX;
    public int maxZ;
    public int minZ;
    public int diagonal2DAboveOrigin;
    public int maxY;
    public int diagonal3D;
    public int diagonal3DAboveOrigin;
    public int itemDropHeight;
    public int vertexData[];
    public int triangleData[];
    public int vertexGroups[][];
    public int faceGroups[][];
    public boolean singleTile;
    public VertexNormal vertexNormalsOffsets[];
    private FaceNormal[] faceNormals;
    static ModelHeader modelHeaders[];
    static boolean hasAnEdgeToRestrict[] = new boolean[6500];
    static boolean outOfReach[] = new boolean[6500];
    static int vertexScreenX[] = new int[6500];
    static int vertexScreenY[] = new int[6500];
    static int vertexScreenZ[] = new int[6500];
    static int vertexMovedX[] = new int[6500];
    static int vertexMovedY[] = new int[6500];
    static int vertexMovedZ[] = new int[6500];
    static int depth[] = new int[1600];
    static int faceLists[][] = new int[1600][512];
    static int anIntArray1673[] = new int[12];
    static int anIntArrayArray1674[][] = new int[12][2000];
    static int anIntArray1676[] = new int[2000];
    static int anIntArray1675[] = new int[2000];
    static int anIntArray1677[] = new int[12];
    static int xPosition[] = new int[10];
    static int yPosition[] = new int[10];
    static int zPosition[] = new int[10];
    static int transformTempX;
    static int transformTempY;
    static int transformTempZ;
    public static boolean objectExist;
    public static int cursorX;
    public static int cursorY;
    public static int objectsHovering;
    public static long hoveringObjects[] = new long[1000];
    public static int SINE[];
    public static int COSINE[];
    static int modelColors[];
    static int modelLocations[];

    HashMap<Integer, net.runelite.api.AABB> aabb = new HashMap<Integer, net.runelite.api.AABB>();

    static {
        SINE = Rasterizer3D.SINE;
        COSINE = Rasterizer3D.COSINE;
        modelColors = Rasterizer3D.hslToRgb;
        modelLocations = Rasterizer3D.anIntArray1469;
    }


    public int bufferOffset;
    public int uvBufferOffset;

    @Override
    public java.util.List<net.runelite.api.model.Vertex> getVertices() {
        int[] verticesX = getVerticesX();
        int[] verticesY = getVerticesY();
        int[] verticesZ = getVerticesZ();
        ArrayList<net.runelite.api.model.Vertex> vertices = new ArrayList<>(getVerticesCount());
        for (int i = 0; i < getVerticesCount(); i++) {
            net.runelite.api.model.Vertex vertex = new net.runelite.api.model.Vertex(verticesX[i], verticesY[i], verticesZ[i]);
            vertices.add(vertex);
        }
        return vertices;
    }

    @Override
    public List<Triangle> getTriangles() {
        int[] trianglesX = getFaceIndices1();
        int[] trianglesY = getFaceIndices2();
        int[] trianglesZ = getFaceIndices3();

        List<Vertex> vertices = getVertices();
        List<Triangle> triangles = new ArrayList<>(getFaceCount());

        for (int i = 0; i < getFaceCount(); ++i)
        {
            int triangleX = trianglesX[i];
            int triangleY = trianglesY[i];
            int triangleZ = trianglesZ[i];

            Triangle triangle = new Triangle(vertices.get(triangleX),vertices.get(triangleY),vertices.get(triangleZ));
            triangles.add(triangle);
        }

        return triangles;
    }

    @Override
    public int getVerticesCount() {
        return verticesCount;
    }

    @Override
    public int[] getVerticesX() {
        return verticesX;
    }

    @Override
    public int[] getVerticesY() {
        return verticesY;
    }

    @Override
    public int[] getVerticesZ() {
        return verticesZ;
    }

    @Override
    public int getFaceCount() {
        return this.trianglesCount;
    }

    @Override
    public int[] getFaceIndices1() {
        return trianglesX;
    }

    @Override
    public int[] getFaceIndices2() {
        return trianglesY;
    }

    @Override
    public int[] getFaceIndices3() {
        return trianglesZ;
    }

    @Override
    public int[] getFaceColors1() {
        return this.colorsX;
    }

    @Override
    public int[] getFaceColors2() {
        return colorsY;
    }

    @Override
    public int[] getFaceColors3() {
        return colorsZ;
    }

    @Override
    public byte[] getFaceTransparencies() {
        return triangleAlpha;
    }

    private int sceneId;

    @Override
    public int getSceneId() {
        return sceneId;
    }

    @Override
    public void setSceneId(int sceneId) {
        this.sceneId = sceneId;
    }

    @Override
    public int getBufferOffset() {
        return bufferOffset;
    }

    @Override
    public void setBufferOffset(int bufferOffset) {
        this.bufferOffset = bufferOffset;
    }

    @Override
    public int getUvBufferOffset() {
        return uvBufferOffset;
    }

    @Override
    public void setUvBufferOffset(int uvBufferOffset) {
        this.uvBufferOffset = uvBufferOffset;
    }

    @Override
    public int getModelHeight() {
        return modelBaseY;
    }

    @Override
    public void animate(int type, int[] list, int x, int y, int z) {

    }

    @Override
    public byte[] getFaceRenderPriorities() {
        return this.renderPriorities;
    }

    @Override
    public int[][] getVertexGroups() {
        return new int[0][];
    }

    @Override
    public int getRadius() {
        return diagonal3DAboveOrigin;
    }

    @Override
    public int getDiameter() {
        return diagonal3D;
    }

    @Override
    public short[] getFaceTextures() {
        return materials;
    }

    @Override
    public void calculateExtreme(int orientation) {
        calculateBoundingBox(orientation);
    }

    public void resetBounds() {
        this.boundsType = 0;
        this.aabb.clear();
    }

    @Override
    public RSModel toSharedModel(boolean b) {
        return null;
    }

    @Override
    public RSModel toSharedSpotAnimModel(boolean b) {
        return null;
    }

    void invalidate() {
        this.vertexNormalsOffsets = null;
        this.normals = null;
        this.faceNormals = null;
        this.isBoundsCalculated = false;
    }

    @Override
    public void rotateY90Ccw() {
        rotate90Degrees();
    }

    @Override
    public void rotateY180Ccw() {
        for (int vert = 0; vert < this.verticesCount; ++vert)
        {
            this.verticesX[vert] = -this.verticesX[vert];
            this.verticesZ[vert] = -this.verticesZ[vert];
        }

        this.resetBounds();
        invalidate();
    }

    @Override
    public void rotateY270Ccw() {
        for (int vert = 0; vert < this.verticesCount; ++vert)
        {
            int var2 = this.verticesZ[vert];
            this.verticesZ[vert] = this.verticesX[vert];
            this.verticesX[vert] = -var2;
        }

        this.resetBounds();
        invalidate();
    }

    @Override
    public int getXYZMag() {
        return diagonal2DAboveOrigin;
    }

    @Override
    public boolean isClickable() {
        return singleTile;
    }

    @Override
    public void interpolateFrames(RSFrames frames, int frameId, RSFrames nextFrames, int nextFrameId, int interval, int intervalCount) {

    }

    @Override
    public int[] getVertexNormalsX() {
        if(vertexNormalsX == null)
            return getVerticesX();
        return vertexNormalsX;
    }

    @Override
    public void setVertexNormalsX(int[] vertexNormalsX) {
        this.vertexNormalsX = vertexNormalsX;
    }

    @Override
    public int[] getVertexNormalsY() {
        if(vertexNormalsY == null)
            return getVerticesY();
        return vertexNormalsY;
    }

    @Override
    public void setVertexNormalsY(int[] vertexNormalsY) {
        this.vertexNormalsY = vertexNormalsY;
    }

    @Override
    public int[] getVertexNormalsZ() {
        if(vertexNormalsZ == null)
            return getVerticesZ();
        return vertexNormalsZ;
    }

    @Override
    public void setVertexNormalsZ(int[] vertexNormalsZ) {
        this.vertexNormalsZ = vertexNormalsZ;
    }

    @Override
    public byte getOverrideAmount() {
        return 0;
    }

    @Override
    public byte getOverrideHue() {
        return 0;
    }

    @Override
    public byte getOverrideSaturation() {
        return 0;
    }

    @Override
    public byte getOverrideLuminance() {
        return 0;
    }

    @Override
    public HashMap<Integer, net.runelite.api.AABB> getAABBMap() {
        return aabb;
    }

    @Override
    public Shape getConvexHull(int localX, int localY, int orientation, int tileHeight) {
        int[] x2d = new int[getVerticesCount()];
        int[] y2d = new int[getVerticesCount()];

        Perspective.modelToCanvas(Client.instance, getVerticesCount(), localX, localY, tileHeight, orientation, getVerticesX(), getVerticesZ(), getVerticesY(), x2d, y2d);

        return Jarvis.convexHull(x2d, y2d);
    }

    @Override
    public float[] getFaceTextureUVCoordinates() {
        if (faceTextureUVCoordinates == null) {
            computeTextureUvCoordinates();
        }
        return faceTextureUVCoordinates;
    }

    @Override
    public void setFaceTextureUVCoordinates(float[] faceTextureUVCoordinates) {
        this.faceTextureUVCoordinates = faceTextureUVCoordinates;
    }

    @Override
    public int getBottomY() {
        return maxY;
    }

    private void vertexNormals()
    {

        if (vertexNormalsX == null)
        {
            int verticesCount = getVerticesCount();

            vertexNormalsX = new int[verticesCount];
            vertexNormalsY = new int[verticesCount];
            vertexNormalsZ = new int[verticesCount];

            int[] trianglesX = getFaceIndices1();
            int[] trianglesY = getFaceIndices2();
            int[] trianglesZ = getFaceIndices3();
            int[] verticesX = getVerticesX();
            int[] verticesY = getVerticesY();
            int[] verticesZ = getVerticesZ();

            for (int i = 0; i < trianglesCount; ++i)
            {
                int var9 = trianglesX[i];
                int var10 = trianglesY[i];
                int var11 = trianglesZ[i];

                int var12 = verticesX[var10] - verticesX[var9];
                int var13 = verticesY[var10] - verticesY[var9];
                int var14 = verticesZ[var10] - verticesZ[var9];
                int var15 = verticesX[var11] - verticesX[var9];
                int var16 = verticesY[var11] - verticesY[var9];
                int var17 = verticesZ[var11] - verticesZ[var9];

                int var18 = var13 * var17 - var16 * var14;
                int var19 = var14 * var15 - var17 * var12;

                int var20;
                for (var20 = var12 * var16 - var15 * var13; var18 > 8192 || var19 > 8192 || var20 > 8192 || var18 < -8192 || var19 < -8192 || var20 < -8192; var20 >>= 1)
                {
                    var18 >>= 1;
                    var19 >>= 1;
                }

                int var21 = (int) Math.sqrt(var18 * var18 + var19 * var19 + var20 * var20);
                if (var21 <= 0)
                {
                    var21 = 1;
                }

                var18 = var18 * 256 / var21;
                var19 = var19 * 256 / var21;
                var20 = var20 * 256 / var21;

                vertexNormalsX[var9] += var18;
                vertexNormalsY[var9] += var19;
                vertexNormalsZ[var9] += var20;

                vertexNormalsX[var10] += var18;
                vertexNormalsY[var10] += var19;
                vertexNormalsZ[var10] += var20;

                vertexNormalsX[var11] += var18;
                vertexNormalsY[var11] += var19;
                vertexNormalsZ[var11] += var20;
            }
        }
    }

    public void computeTextureUvCoordinates()
    {
        final short[] faceTextures = getFaceTextures();
        if (faceTextures == null)
        {
            return;
        }

        final int[] vertexPositionsX = getVertexNormalsX();
        final int[] vertexPositionsY = getVertexNormalsY();
        final int[] vertexPositionsZ = getVertexNormalsZ();

        final int[] trianglePointsX = getFaceIndices1();
        final int[] trianglePointsY = getFaceIndices2();
        final int[] trianglePointsZ = getFaceIndices3();

        final short[] texTriangleX = texturesX;
        final short[] texTriangleY = texturesY;
        final short[] texTriangleZ = texturesZ;

        final byte[] textureCoords = textures;

        int faceCount = trianglesCount;
        float[] faceTextureUCoordinates = new float[faceCount * 6];

        for (int i = 0; i < faceCount; i++)
        {
            int trianglePointX = trianglePointsX[i];
            int trianglePointY = trianglePointsY[i];
            int trianglePointZ = trianglePointsZ[i];

            short textureIdx = faceTextures[i];

            if (textureIdx != -1)
            {
                int triangleVertexIdx1;
                int triangleVertexIdx2;
                int triangleVertexIdx3;

                if (textureCoords != null && textureCoords[i] != -1)
                {
                    int textureCoordinate = textureCoords[i] & 255;
                    triangleVertexIdx1 = texTriangleX[textureCoordinate];
                    triangleVertexIdx2 = texTriangleY[textureCoordinate];
                    triangleVertexIdx3 = texTriangleZ[textureCoordinate];
                }
                else
                {
                    triangleVertexIdx1 = trianglePointX;
                    triangleVertexIdx2 = trianglePointY;
                    triangleVertexIdx3 = trianglePointZ;
                }

                float triangleX = (float) vertexPositionsX[triangleVertexIdx1];
                float triangleY = (float) vertexPositionsY[triangleVertexIdx1];
                float triangleZ = (float) vertexPositionsZ[triangleVertexIdx1];

                float f_882_ = (float) vertexPositionsX[triangleVertexIdx2] - triangleX;
                float f_883_ = (float) vertexPositionsY[triangleVertexIdx2] - triangleY;
                float f_884_ = (float) vertexPositionsZ[triangleVertexIdx2] - triangleZ;
                float f_885_ = (float) vertexPositionsX[triangleVertexIdx3] - triangleX;
                float f_886_ = (float) vertexPositionsY[triangleVertexIdx3] - triangleY;
                float f_887_ = (float) vertexPositionsZ[triangleVertexIdx3] - triangleZ;
                float f_888_ = (float) vertexPositionsX[trianglePointX] - triangleX;
                float f_889_ = (float) vertexPositionsY[trianglePointX] - triangleY;
                float f_890_ = (float) vertexPositionsZ[trianglePointX] - triangleZ;
                float f_891_ = (float) vertexPositionsX[trianglePointY] - triangleX;
                float f_892_ = (float) vertexPositionsY[trianglePointY] - triangleY;
                float f_893_ = (float) vertexPositionsZ[trianglePointY] - triangleZ;
                float f_894_ = (float) vertexPositionsX[trianglePointZ] - triangleX;
                float f_895_ = (float) vertexPositionsY[trianglePointZ] - triangleY;
                float f_896_ = (float) vertexPositionsZ[trianglePointZ] - triangleZ;

                float f_897_ = f_883_ * f_887_ - f_884_ * f_886_;
                float f_898_ = f_884_ * f_885_ - f_882_ * f_887_;
                float f_899_ = f_882_ * f_886_ - f_883_ * f_885_;
                float f_900_ = f_886_ * f_899_ - f_887_ * f_898_;
                float f_901_ = f_887_ * f_897_ - f_885_ * f_899_;
                float f_902_ = f_885_ * f_898_ - f_886_ * f_897_;
                float f_903_ = 1.0F / (f_900_ * f_882_ + f_901_ * f_883_ + f_902_ * f_884_);

                float u0 = (f_900_ * f_888_ + f_901_ * f_889_ + f_902_ * f_890_) * f_903_;
                float u1 = (f_900_ * f_891_ + f_901_ * f_892_ + f_902_ * f_893_) * f_903_;
                float u2 = (f_900_ * f_894_ + f_901_ * f_895_ + f_902_ * f_896_) * f_903_;

                f_900_ = f_883_ * f_899_ - f_884_ * f_898_;
                f_901_ = f_884_ * f_897_ - f_882_ * f_899_;
                f_902_ = f_882_ * f_898_ - f_883_ * f_897_;
                f_903_ = 1.0F / (f_900_ * f_885_ + f_901_ * f_886_ + f_902_ * f_887_);

                float v0 = (f_900_ * f_888_ + f_901_ * f_889_ + f_902_ * f_890_) * f_903_;
                float v1 = (f_900_ * f_891_ + f_901_ * f_892_ + f_902_ * f_893_) * f_903_;
                float v2 = (f_900_ * f_894_ + f_901_ * f_895_ + f_902_ * f_896_) * f_903_;

                int idx = i * 6;
                faceTextureUCoordinates[idx] = u0;
                faceTextureUCoordinates[idx + 1] = v0;
                faceTextureUCoordinates[idx + 2] = u1;
                faceTextureUCoordinates[idx + 3] = v1;
                faceTextureUCoordinates[idx + 4] = u2;
                faceTextureUCoordinates[idx + 5] = v2;
            }
        }

        faceTextureUVCoordinates = faceTextureUCoordinates;
    }

    public int lastOrientation = -1;

    @Override
    public int getLastOrientation() {
        return lastOrientation;
    }

    @Override
    public AABB getAABB(int orientation) {
        calculateExtreme(orientation);
        lastOrientation = orientation;
        return (AABB) getAABBMap().get(lastOrientation);
    }

}
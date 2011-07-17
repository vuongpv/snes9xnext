/**
 * GENPlusDroid
 * Copyright 2011 Stephen Damm (Halsafar)
 * All rights reserved.
 * shinhalsafar@gmail.com
 *
 * Graphics.h
 *
 *  Created on: Nov 4, 2010
 *      Author: halsafar
 */

#ifndef Graphics_H_
#define Graphics_H_

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

#include "libzip/zip.h"
#include "libpng/png.h"
#include "libpng/pngstruct.h"

#include "common.h"
#include "Quad.h"


#define SCREEN_16_9_ASPECT_RATIO (16.0/9.0)
#define SCREEN_4_3_ASPECT_RATIO (4.0/3.0)

#define SNES_RENDER_TEXTURE_WIDTH  320.0
#define SNES_RENDER_TEXTURE_HEIGHT 224.0
#define SCREEN_RENDER_TEXTURE_WIDTH  512.0
#define SCREEN_RENDER_TEXTURE_HEIGHT 512.0
#define SCREEN_RENDER_BYTE_BY_PIXEL  4

#define GRAPHICS_OK 0
#define GRAPHICS_ERROR 1


// PC Palette
struct pcpal {
    unsigned char r;
    unsigned char g;
    unsigned char b;
    unsigned char a;
};

class GraphicsDriver
{
public:
     GraphicsDriver();
	~GraphicsDriver();

	int Init();
	void Destroy();

	int32_t InitEmuShader(const char* vertex, const char* frag);

	void SetDimensions(int width, int height);
	void SetViewportOffsets(int xOffset, int yOffset);
	void SetOverscan(bool bOverscan, float amount);
	void SetAspectRatio(float ratio);
	void SetSmooth(bool smooth);
	void Clear() const;
	void DrawQuad2D(Quad2D* quad) const;
	void Draw();
	void DrawEmu(uint16_t *XBuf, int nesw, int nesh);
	void Swap() const;
	
	void ReshapeEmuTexture(int width, int height);
	GLint GenTexture();
	int BlitToTexture(int texId, int width, int height, int bitDepth, void* imageData);
	int loadPNGTexture(zip* apkArchive, const char* szFilename, int* outGLId);

	float getScreenWidth() { return _screenWidth; }
	float getScreenHeight() { return _screenHeight; }
private:
	int _screenWidth;
	int _screenHeight;
	float _realWidth;
	float _realHeight;
	float _xOffset;
	float _yOffset;

	float _emuWidth;
	float _emuHeight;

	float _guiAlpha;

	float _ratio;
	bool _smooth;

	GLuint _textureId;
	float* _matrix;
	GLfloat* _vertices;
	GLfloat* _texCoords;

	GLint _shaderProgram;
	GLint _vMvpMatrix;
	GLint _vPositionHandle;
	GLint _vTexCoordHandle;
	GLint _vSamplerHandle;
	GLint _vAlphaHandle;

     GLint _EmuShaderProgram;
     GLint _vEmuMvpMatrix;
     GLint _vEmuPositionHandle;
     GLint _vEmuTexCoordHandle;
     GLint _vEmuSamplerHandle;
     GLint _vEmuTextureSizeXHandle;
     GLint _vEmuTextureSizeYHandle;
     GLint _vEmuPaletteSamplerHandle;

	GLuint CreateProgram(const char* pVertexSource, const char* pFragmentSource);
	GLuint LoadShader(GLenum shaderType, const char* pSource);
	void SetAspect();
	void _setupScene();
};



#endif /* Graphics_H_ */
